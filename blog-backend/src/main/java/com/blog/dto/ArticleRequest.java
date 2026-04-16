package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArticleRequest {
    @NotBlank(message = "文章标题不能为空")
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String category;
    private String tags;
    private Integer status = 1;
}
