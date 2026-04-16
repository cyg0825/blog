package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    /**
     * 公开统计接口，无需登录，用于首页数字展示
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> publicStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArticles", articleRepository.countByStatus(1));
        stats.put("totalUsers", userRepository.count());
        stats.put("totalComments", commentRepository.countByStatus(1));
        return ApiResponse.ok(stats);
    }
}
