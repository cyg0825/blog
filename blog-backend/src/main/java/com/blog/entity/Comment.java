package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "parent_id")
    private Long parentId;

    // 0=pending, 1=approved, 2=rejected
    @Column(nullable = false)
    private Integer status = 1;

    // AI审查结果: null=未审查, 1=通过, 0=拦截
    @Column(name = "ai_review_result")
    private Integer aiReviewResult;

    @Column(name = "ai_review_reason", length = 500)
    private String aiReviewReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
