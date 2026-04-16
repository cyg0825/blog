package com.blog.controller;

import com.blog.dto.*;
import com.blog.entity.User;
import com.blog.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("注册成功", userService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> currentUser(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(UserDTO.from(user));
    }

    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(@AuthenticationPrincipal User user,
                                               @RequestBody UserDTO dto) {
        return ApiResponse.ok("更新成功", userService.updateProfile(user.getId(), dto));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal User user,
                                             @RequestBody Map<String, String> body) {
        userService.changePassword(user.getId(), body.get("oldPassword"), body.get("newPassword"));
        return ApiResponse.ok("密码修改成功", null);
    }
}
