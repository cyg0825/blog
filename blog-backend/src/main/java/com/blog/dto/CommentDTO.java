package com.blog.dto;

import com.blog.entity.Comment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long articleId;
    private String articleTitle;
    private UserDTO author;
    private Long parentId;
    private Integer status;
    private Integer aiReviewResult;
    private String aiReviewReason;
    private LocalDateTime createdAt;

    public static CommentDTO from(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setStatus(comment.getStatus());
        dto.setAiReviewResult(comment.getAiReviewResult());
        dto.setAiReviewReason(comment.getAiReviewReason());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
            dto.setArticleTitle(comment.getArticle().getTitle());
        }
        if (comment.getAuthor() != null) {
            dto.setAuthor(UserDTO.from(comment.getAuthor()));
        }
        return dto;
    }
}
