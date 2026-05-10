package com.habitame.api.user.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.user.dto.UserActiveRequest;
import com.habitame.api.user.dto.UserFilter;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(new UserFilter(role, isActive), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<UserResponse> setActive(
            @PathVariable Integer id,
            @RequestBody UserActiveRequest request) {
        return ResponseEntity.ok(userService.setActive(id, request.active()));
    }
}
