package com.blog.controller;

import com.blog.dto.*;
import com.blog.entity.SystemConfig;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import com.blog.service.impl.ArticleService;
import com.blog.service.impl.CommentService;
import com.blog.service.impl.SystemConfigService;
import com.blog.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;
    private final SystemConfigService configService;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    // ===== 统计概览 =====
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalArticles", articleRepository.count());
        stats.put("totalComments", commentRepository.count());
        stats.put("publishedArticles", articleRepository.countByStatus(1));
        stats.put("pendingComments", commentRepository.countByStatus(0));
        stats.put("blockedComments", commentRepository.countByStatus(2));
        stats.put("aiBlockedComments", commentRepository.countByAiReviewResult(0));
        return ApiResponse.ok(stats);
    }

    // ===== 用户管理 =====
    @GetMapping("/users")
    public ApiResponse<Page<UserDTO>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.ok(userService.searchUsers(keyword, status, pageable));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ApiResponse.ok("更新成功", userService.adminUpdateUser(id, dto));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/users/{id}/toggle-status")
    public ApiResponse<UserDTO> toggleUserStatus(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        return ApiResponse.ok("操作成功", userService.adminUpdateUser(id, user));
    }

    // ===== 文章管理 =====
    @GetMapping("/articles")
    public ApiResponse<Page<ArticleDTO>> articles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.ok(articleService.searchArticles(keyword, category, status, null, pageable));
    }

    @DeleteMapping("/articles/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long id) {
        // Admin can delete any article
        articleRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PutMapping("/articles/{id}/status")
    public ApiResponse<Void> updateArticleStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, Integer> body) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setStatus(body.get("status"));
            articleRepository.save(article);
        });
        return ApiResponse.ok("状态更新成功", null);
    }

    // ===== 评论管理 =====
    @GetMapping("/comments")
    public ApiResponse<Page<CommentDTO>> comments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.ok(commentService.searchComments(keyword, status, articleId, pageable));
    }

    @PutMapping("/comments/{id}/status")
    public ApiResponse<CommentDTO> updateCommentStatus(@PathVariable Long id,
                                                        @RequestBody Map<String, Integer> body) {
        return ApiResponse.ok("状态更新成功", commentService.updateCommentStatus(id, body.get("status")));
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }

    // ===== 系统配置 =====
    @GetMapping("/configs")
    public ApiResponse<List<SystemConfig>> configs() {
        return ApiResponse.ok(configService.getAll());
    }

    @PostMapping("/configs")
    public ApiResponse<SystemConfig> createConfig(@RequestBody SystemConfig config) {
        return ApiResponse.ok("创建成功", configService.createConfig(config));
    }

    @PutMapping("/configs/{id}")
    public ApiResponse<SystemConfig> updateConfig(@PathVariable Long id,
                                                   @RequestBody SystemConfig config) {
        return ApiResponse.ok("更新成功", configService.updateConfig(id, config));
    }

    @DeleteMapping("/configs/{id}")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return ApiResponse.ok("删除成功", null);
    }
}
