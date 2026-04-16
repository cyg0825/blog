package com.blog.dto;

import com.blog.entity.Article;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String category;
    private String tags;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private UserDTO author;
    private String aiSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;

    public static ArticleDTO from(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCoverImage(article.getCoverImage());
        dto.setCategory(article.getCategory());
        dto.setTags(article.getTags());
        dto.setStatus(article.getStatus());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setAiSummary(article.getAiSummary());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        if (article.getAuthor() != null) {
            dto.setAuthor(UserDTO.from(article.getAuthor()));
        }
        return dto;
    }
}
