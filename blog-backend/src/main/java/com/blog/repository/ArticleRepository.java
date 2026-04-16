package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a WHERE " +
           "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.summary LIKE %:keyword%) " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:authorId IS NULL OR a.author.id = :authorId) " +
           "ORDER BY a.createdAt DESC")
    Page<Article> searchArticles(@Param("keyword") String keyword,
                                  @Param("category") String category,
                                  @Param("status") Integer status,
                                  @Param("authorId") Long authorId,
                                  Pageable pageable);

    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.category IS NOT NULL AND a.status = 1")
    List<String> findAllCategories();

    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    List<Article> findTop5ByStatusOrderByViewCountDesc(Integer status);

    long countByAuthorId(Long authorId);
    long countByStatus(Integer status);
}
