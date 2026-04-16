package com.blog.controller;

import com.blog.dto.*;
import com.blog.entity.User;
import com.blog.service.impl.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<Page<ArticleDTO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.ok(articleService.searchArticles(keyword, category, status, authorId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleDTO> detail(@PathVariable Long id) {
        return ApiResponse.ok(articleService.getArticleDetail(id, true));
    }

    @PostMapping
    public ApiResponse<ArticleDTO> create(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.ok("发布成功", articleService.createArticle(request, user.getUsername()));
    }

    @PutMapping("/{id}")
    public ApiResponse<ArticleDTO> update(@PathVariable Long id,
                                          @AuthenticationPrincipal User user,
                                          @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.ok("更新成功", articleService.updateArticle(id, request, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal User user) {
        articleService.deleteArticle(id, user.getUsername());
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/{id}/ai-summary")
    public ApiResponse<String> aiSummary(@PathVariable Long id) {
        return ApiResponse.ok(articleService.generateAiSummary(id));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        articleService.likeArticle(id);
        return ApiResponse.ok("点赞成功", null);
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(articleService.getAllCategories());
    }

    @GetMapping("/hot")
    public ApiResponse<List<ArticleDTO>> hot() {
        return ApiResponse.ok(articleService.getHotArticles());
    }
}
