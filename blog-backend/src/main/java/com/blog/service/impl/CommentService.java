package com.blog.service.impl;

import com.blog.ai.AiService;
import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    @Transactional
    public CommentDTO addComment(Long articleId, String content, Long parentId, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticle(article);
        comment.setAuthor(author);
        comment.setParentId(parentId);

        // AI 审查
        try {
            AiService.CommentReviewResult reviewResult = aiService.reviewComment(content);
            comment.setAiReviewResult(reviewResult.pass() ? 1 : 0);
            comment.setAiReviewReason(reviewResult.reason());
            if (!reviewResult.pass()) {
                comment.setStatus(2); // 被拦截
                Comment saved = commentRepository.save(comment);
                CommentDTO dto = CommentDTO.from(saved);
                dto.setContent(""); // 不返回被拦截的内容
                throw new RuntimeException("评论内容不符合规范：" + reviewResult.reason());
            }
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("评论内容不符合规范")) throw e;
            log.warn("AI审查异常，默认通过: {}", e.getMessage());
            comment.setAiReviewResult(1);
            comment.setAiReviewReason("AI服务异常，默认通过");
        }

        comment.setStatus(1);
        return CommentDTO.from(commentRepository.save(comment));
    }

    public List<CommentDTO> getArticleComments(Long articleId) {
        return commentRepository.findByArticleIdAndStatusOrderByCreatedAtAsc(articleId, 1)
                .stream().map(CommentDTO::from).toList();
    }

    public Page<CommentDTO> searchComments(String keyword, Integer status, Long articleId, Pageable pageable) {
        return commentRepository.searchComments(keyword, status, articleId, pageable)
                .map(CommentDTO::from);
    }

    @Transactional
    public CommentDTO updateCommentStatus(Long id, Integer status) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setStatus(status);
        return CommentDTO.from(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!comment.getAuthor().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权限删除此评论");
        }
        commentRepository.deleteById(id);
    }
}
