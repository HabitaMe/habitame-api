package com.habitame.api.user.service;

import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
