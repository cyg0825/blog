package com.blog.service.impl;

import com.blog.ai.AiService;
import com.blog.dto.*;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", UserDTO.from(user));
        return result;
    }

    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername());
        return UserDTO.from(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateProfile(Long userId, UserDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
                
        // 检查用户名是否要更新（用户名一旦设置就不允许更改，以避免安全问题）
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            // 只有在特殊情况下才允许更改用户名，例如首次设置或管理员操作
            // 这里我们禁止普通用户更改用户名
            throw new RuntimeException("用户名不允许更改，以保证账户安全和稳定性");
        }
        
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        return UserDTO.from(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Page<UserDTO> searchUsers(String keyword, Integer status, Pageable pageable) {
        return userRepository.searchUsers(keyword, status, pageable).map(UserDTO::from);
    }

    @Transactional
    public UserDTO adminUpdateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        return UserDTO.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO getUserById(Long id) {
        return UserDTO.from(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在")));
    }
}