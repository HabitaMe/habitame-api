package com.habitame.api.user.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.user.dto.UserFilter;
import com.habitame.api.user.dto.UserRequest;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import com.habitame.api.user.repository.UserSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public UserEntity findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public PageResponse<UserResponse> findAll(UserFilter filter, Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(UserSpecification.filter(filter), pageable);
        return new PageResponse<>(
                page.map(UserMapper::toResponse).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public UserResponse findUserById(Integer id) {
        return UserMapper.toResponse(findById(id));
    }

    @Transactional
    public UserResponse setActive(Integer id, boolean active) {
        UserEntity user = findById(id);
        user.setIsActive(active);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public UserResponse addPhoto(Integer userId, @Valid MultipartFile file) throws IOException {

        if (!userId.equals(SecurityUtils.getCurrentUserId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You are not authorized to add a photo to this user");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (file.isEmpty() || !isImage(file)) {
            throw new IllegalArgumentException("Invalid file: must be a non-empty image file");
        }

        String url = imageStorageService.store(file, "profiles");

        user.setPhotoUrl(url);

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public UserResponse removePhoto(Integer idUser) throws IOException {
        if (!idUser.equals(SecurityUtils.getCurrentUserId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You are not authorized to remove a photo from this user");
        }
        UserEntity user = userRepository.findById(idUser).orElseThrow(() -> new ResourceNotFoundException("User not found: " + idUser));
        imageStorageService.delete(user.getPhotoUrl());
        user.setPhotoUrl(null);
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    public UserResponse updateUser(@Valid UserRequest request) {
        UserEntity user = SecurityUtils.getCurrentUser();
        user.setFullName(request.fullname());
        user.setPhone(request.phone());
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }
}
