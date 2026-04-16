package com.blog.controller;

import com.blog.dto.*;
import com.blog.entity.User;
import com.blog.service.impl.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/article/{articleId}")
    public ApiResponse<List<CommentDTO>> articleComments(@PathVariable Long articleId) {
        return ApiResponse.ok(commentService.getArticleComments(articleId));
    }

    @PostMapping("/article/{articleId}")
    public ApiResponse<CommentDTO> addComment(@PathVariable Long articleId,
                                               @AuthenticationPrincipal User user,
                                               @RequestBody Map<String, Object> body) {
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        return ApiResponse.ok("评论成功", commentService.addComment(articleId, content, parentId, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal User user) {
        commentService.deleteComment(id, user.getUsername());
        return ApiResponse.ok("删除成功", null);
    }
}
