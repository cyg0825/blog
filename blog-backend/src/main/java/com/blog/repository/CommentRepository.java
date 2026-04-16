package com.blog.repository;

import com.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleIdAndStatusOrderByCreatedAtAsc(Long articleId, Integer status);

    @Query("SELECT c FROM Comment c WHERE " +
           "(:keyword IS NULL OR c.content LIKE %:keyword%) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:articleId IS NULL OR c.article.id = :articleId) " +
           "ORDER BY c.createdAt DESC")
    Page<Comment> searchComments(@Param("keyword") String keyword,
                                  @Param("status") Integer status,
                                  @Param("articleId") Long articleId,
                                  Pageable pageable);

    long countByArticleId(Long articleId);
    long countByStatus(Integer status);
    long countByAiReviewResult(Integer result);
}
