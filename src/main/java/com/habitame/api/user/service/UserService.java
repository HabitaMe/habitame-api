package com.habitame.api.user.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.PropertyImageMapper;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public UserEntity findById(Integer id) {
        return userRepository.findById(id).orElse(null);
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
}
