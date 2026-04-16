package com.blog.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AiService {

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通用 AI 调用
     */
    private String callAi(String systemPrompt, String userContent, int maxTokens) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", maxTokens);
            body.put("temperature", 0.3);

            ArrayNode messages = body.putArray("messages");
            ObjectNode sysMsg = messages.addObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);

            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);

            RequestBody requestBody = RequestBody.create(
                    objectMapper.writeValueAsString(body),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode choices = jsonNode.get("choices");
                    if (choices != null && choices.size() > 0) {
                        return choices.get(0).get("message").get("content").asText().trim();
                    }
                } else {
                    log.warn("AI API 返回非成功状态: {}", response.code());
                }
            }
        } catch (Exception e) {
            log.error("AI API 调用异常: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 评论内容 AI 审查
     * 返回 CommentReviewResult { pass, reason }
     */
    public CommentReviewResult reviewComment(String commentContent) {
        if (commentContent == null || commentContent.isBlank()) {
            return new CommentReviewResult(false, "评论内容不能为空");
        }

        String systemPrompt = """
                你是一个评论内容审查助手，负责检测博客评论中的违规内容。
                审查标准：
                1. 包含辱骂、人身攻击、恶意中伤 → 不通过
                2. 包含色情、暴力、赌博等违法内容 → 不通过
                3. 纯广告、刷屏、垃圾信息 → 不通过
                4. 政治敏感、散布谣言 → 不通过
                5. 正常技术讨论、学习交流、善意提问 → 通过
                
                请严格以 JSON 格式返回，不要有任何其他文字：
                {"pass": true, "reason": "内容正常，符合社区规范"}
                或
                {"pass": false, "reason": "具体说明不通过的原因"}
                """;

        String result = callAi(systemPrompt, "请审查以下评论内容：\n" + commentContent, 200);

        if (result == null) {
            log.warn("AI审查服务不可用，评论默认通过");
            return new CommentReviewResult(true, "AI审查服务暂不可用，默认通过");
        }

        try {
            // 清理 markdown 代码块包装
            String cleaned = result.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("(?s)```[a-z]*\\n?", "").replaceAll("```", "").trim();
            }
            // 提取 JSON 部分（防止有额外说明文字）
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }
            JsonNode node = objectMapper.readTree(cleaned);
            boolean pass = node.path("pass").asBoolean(true);
            String reason = node.path("reason").asText("审查完成");
            return new CommentReviewResult(pass, reason);
        } catch (Exception e) {
            log.warn("解析AI审查结果失败，原始返回: [{}], 错误: {}", result, e.getMessage());
            return new CommentReviewResult(true, "审查结果解析异常，默认通过");
        }
    }

    /**
     * 博客文章 AI 摘要生成
     */
    public String summarizeArticle(String title, String articleContent) {
        if (articleContent == null || articleContent.isBlank()) {
            return null;
        }
        // 去除 Markdown 语法后截取，避免超 token
        String plainText = articleContent
                .replaceAll("```[\\s\\S]*?```", "[代码块]")
                .replaceAll("`[^`]+`", "")
                .replaceAll("#+ ", "")
                .replaceAll("\\*\\*([^*]+)\\*\\*", "$1")
                .replaceAll("\\*([^*]+)\\*", "$1")
                .replaceAll("!\\[[^]]*]\\([^)]*\\)", "")
                .replaceAll("\\[[^]]*]\\([^)]*\\)", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();

        if (plainText.length() > 2500) {
            plainText = plainText.substring(0, 2500) + "...（内容过长，已截取前段）";
        }

        String systemPrompt = """
                你是一位专业的技术博客摘要助手。
                请对用户提供的博客文章进行智能总结，要求：
                1. 提炼文章核心主题和主要知识点
                2. 语言简洁流畅，100-200字中文
                3. 突出文章的实用价值和技术亮点
                4. 不要复述标题，直接从内容价值入手
                5. 直接输出摘要文本，无需任何前缀
                """;

        String userContent = "文章标题：" + (title != null ? title : "") + "\n\n文章正文：\n" + plainText;
        String result = callAi(systemPrompt, userContent, 400);

        if (result == null || result.isBlank()) {
            return null;
        }
        return result;
    }

    public record CommentReviewResult(boolean pass, String reason) {}
}
