package com.blog.service.impl;

import com.blog.ai.AiService;
import com.blog.dto.*;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AiService aiService;

    public Page<ArticleDTO> searchArticles(String keyword, String category,
                                            Integer status, Long authorId, Pageable pageable) {
        Page<ArticleDTO> page = articleRepository.searchArticles(keyword, category, status, authorId, pageable)
                .map(article -> {
                    ArticleDTO dto = ArticleDTO.from(article);
                    dto.setCommentCount(commentRepository.countByArticleId(article.getId()));
                    return dto;
                });
        return page;
    }

    @Transactional
    public ArticleDTO getArticleDetail(Long id, boolean incrementView) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        if (incrementView) {
            articleRepository.incrementViewCount(id);
            article.setViewCount(article.getViewCount() + 1);
        }
        ArticleDTO dto = ArticleDTO.from(article);
        dto.setCommentCount(commentRepository.countByArticleId(id));
        return dto;
    }

    @Transactional
    public ArticleDTO createArticle(ArticleRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Article article = new Article();
        mapRequestToArticle(request, article);
        article.setAuthor(author);
        return ArticleDTO.from(articleRepository.save(article));
    }

    @Transactional
    public ArticleDTO updateArticle(Long id, ArticleRequest request, String username) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!article.getAuthor().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权限修改此文章");
        }
        mapRequestToArticle(request, article);
        // 内容更新后清除AI摘要，下次访问重新生成
        if (request.getContent() != null) {
            article.setAiSummary(null);
        }
        return ArticleDTO.from(articleRepository.save(article));
    }

    @Transactional
    public void deleteArticle(Long id, String username) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!article.getAuthor().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权限删除此文章");
        }
        articleRepository.deleteById(id);
    }

    @Transactional
    public String generateAiSummary(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        if (article.getAiSummary() != null && !article.getAiSummary().isBlank()) {
            return article.getAiSummary();
        }
        String summary = aiService.summarizeArticle(article.getTitle(), article.getContent());
        if (summary != null) {
            article.setAiSummary(summary);
            articleRepository.save(article);
        }
        return summary;
    }

    @Transactional
    public void likeArticle(Long id) {
        articleRepository.incrementLikeCount(id);
    }

    public List<String> getAllCategories() {
        return articleRepository.findAllCategories();
    }

    public List<ArticleDTO> getHotArticles() {
        return articleRepository.findTop5ByStatusOrderByViewCountDesc(1)
                .stream().map(ArticleDTO::from).toList();
    }

    private void mapRequestToArticle(ArticleRequest request, Article article) {
        article.setTitle(request.getTitle());
        if (request.getContent() != null) article.setContent(request.getContent());
        if (request.getSummary() != null) article.setSummary(request.getSummary());
        if (request.getCoverImage() != null) article.setCoverImage(request.getCoverImage());
        if (request.getCategory() != null) article.setCategory(request.getCategory());
        if (request.getTags() != null) article.setTags(request.getTags());
        if (request.getStatus() != null) article.setStatus(request.getStatus());
    }
}
