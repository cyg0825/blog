package com.blog.config;

import com.blog.entity.*;
import com.blog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final SystemConfigRepository configRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }
        log.info("开始初始化演示数据...");
        String pwd = passwordEncoder.encode("admin123");

        // 创建用户
        User admin = createUser("admin", pwd, "admin@blog.com", "ADMIN",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=admin",
                "系统管理员，负责平台日常运营与维护。");
        User zhangwei = createUser("zhangwei", pwd, "zhangwei@example.com", "USER",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=zhangwei",
                "全栈开发工程师，热爱技术分享，专注 Java + Vue 方向。");
        User lihua = createUser("lihua", pwd, "lihua@example.com", "USER",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=lihua",
                "前端开发者，Vue 爱好者，关注用户体验与界面设计。");
        User wangfang = createUser("wangfang", pwd, "wangfang@example.com", "USER",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=wangfang",
                "后端架构师，Spring Boot 深度用户，研究分布式系统。");
        User chenxiao = createUser("chenxiao", pwd, "chenxiao@example.com", "USER",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=chenxiao",
                "大四学生，准备毕业设计，记录学习历程。");

        // 创建系统配置
        saveConfig("site_name", "技术博客平台", "网站名称");
        saveConfig("site_description", "分享技术，记录生活，连接开发者", "网站描述");
        saveConfig("ai_review_enabled", "true", "是否开启AI评论审查");
        saveConfig("ai_summary_enabled", "true", "是否开启AI文章总结");
        saveConfig("allow_register", "true", "是否允许注册");
        saveConfig("footer_text", "© 2026 技术博客平台. All Rights Reserved.", "页脚文本");
        saveConfig("max_comment_length", "1000", "评论最大字符数");

        // 创建文章
        Article a1 = createArticle("Spring Boot 3.x 快速入门指南",
                "## 前言\n\nSpring Boot 3.x 是目前最流行的 Java 后端框架，本文带你从零搭建完整项目。\n\n## 环境准备\n\n- JDK 17+\n- Maven 3.6+\n- IntelliJ IDEA\n\n## 创建项目\n\n通过 Spring Initializr 快速创建项目骨架，选择 Web、JPA、Security 依赖。\n\n## 第一个接口\n\n```java\n@RestController\n@RequestMapping(\"/api\")\npublic class HelloController {\n    @GetMapping(\"/hello\")\n    public String hello() {\n        return \"Hello, Spring Boot 3!\";\n    }\n}\n```\n\n## 配置文件\n\n`application.yml` 是核心配置文件，支持 YAML 层次结构。\n\n```yaml\nserver:\n  port: 8080\nspring:\n  datasource:\n    url: jdbc:h2:mem:testdb\n```\n\n## 自动配置原理\n\nSpring Boot 通过 `@EnableAutoConfiguration` 扫描 `META-INF/spring.factories` 文件，自动装配符合条件的 Bean。\n\n## 总结\n\nSpring Boot 极大简化了项目配置，让开发者专注于业务逻辑的实现，是 Java 后端开发的最佳选择。",
                "从零搭建 Spring Boot 3.x 项目，含环境准备、接口编写和配置详解。",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800",
                "Java技术", "Spring Boot,Java,后端开发", zhangwei, 1286, 42);

        Article a2 = createArticle("Vue 3 Composition API 实战详解",
                "## 什么是 Composition API\n\nComposition API 是 Vue 3 引入的全新编程范式，相比 Options API 更加灵活，代码复用性更强。\n\n## ref 与 reactive\n\n```javascript\nimport { ref, reactive, computed } from 'vue';\n\nconst count = ref(0);\nconst user = reactive({ name: '张三', age: 25 });\nconst doubleCount = computed(() => count.value * 2);\n```\n\n## script setup 语法糖\n\n```vue\n<script setup>\nimport { ref, onMounted } from 'vue';\nconst count = ref(0);\nonMounted(() => console.log('组件挂载完成'));\n</script>\n<template>\n  <div>{{ count }}</div>\n</template>\n```\n\n## 自定义 Hook\n\n```javascript\n// useCounter.js\nexport function useCounter(initial = 0) {\n  const count = ref(initial);\n  const increment = () => count.value++;\n  const decrement = () => count.value--;\n  return { count, increment, decrement };\n}\n```\n\n## 总结\n\nComposition API 让代码组织更清晰，是 Vue 3 最重要的特性之一。",
                "深入解析 Vue 3 Composition API，含 ref/reactive/computed 及自定义 Hook 实战。",
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800",
                "前端技术", "Vue3,前端,JavaScript", lihua, 987, 35);

        Article a3 = createArticle("MySQL 数据库性能优化实践",
                "## 索引优化\n\n索引是提升查询性能的最有效手段。\n\n```sql\n-- 为高频查询字段建立索引\nCREATE INDEX idx_email ON users(email);\nCREATE INDEX idx_article_status ON articles(status, created_at);\n```\n\n## EXPLAIN 分析\n\n```sql\nEXPLAIN SELECT * FROM articles WHERE status = 1 ORDER BY created_at DESC;\n```\n\n关注 `type` 字段：\n- `ALL` → 全表扫描（差）\n- `index` → 索引扫描\n- `ref` → 非唯一索引\n- `const` → 主键或唯一索引（最优）\n\n## 慢查询优化\n\n开启慢查询日志，定位问题 SQL。\n\n```sql\nSET GLOBAL slow_query_log = ON;\nSET GLOBAL long_query_time = 1;\n```\n\n## 分页优化\n\n大数据量分页用游标替代 LIMIT OFFSET：\n\n```sql\n-- 慢（offset 很大时）\nSELECT * FROM articles LIMIT 10000, 10;\n-- 快（游标方式）\nSELECT * FROM articles WHERE id > 10000 LIMIT 10;\n```\n\n## 总结\n\n合理的索引设计和 SQL 习惯是数据库性能的基础。",
                "总结 MySQL 数据库性能优化实践，含索引设计、EXPLAIN 解读、慢查询分析等核心技巧。",
                "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=800",
                "数据库", "MySQL,数据库,性能优化", wangfang, 756, 28);

        Article a4 = createArticle("Docker 容器化部署完整指南",
                "## Docker 基本概念\n\n- **镜像(Image)**：只读模板，包含运行环境\n- **容器(Container)**：镜像的运行实例\n- **仓库(Registry)**：镜像存储与分发中心\n\n## Dockerfile 编写\n\n```dockerfile\nFROM openjdk:17-jre-slim\nWORKDIR /app\nCOPY target/*.jar app.jar\nEXPOSE 8080\nENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]\n```\n\n## docker-compose 编排\n\n```yaml\nversion: '3'\nservices:\n  app:\n    build: .\n    ports:\n      - \"8080:8080\"\n    depends_on:\n      - mysql\n  mysql:\n    image: mysql:8\n    environment:\n      MYSQL_ROOT_PASSWORD: root\n      MYSQL_DATABASE: blogdb\n    volumes:\n      - mysql-data:/var/lib/mysql\nvolumes:\n  mysql-data:\n```\n\n## 常用命令\n\n```bash\ndocker build -t myapp:1.0 .\ndocker run -d -p 8080:8080 myapp:1.0\ndocker-compose up -d\ndocker logs -f container_name\n```\n\n## 总结\n\nDocker 已成为现代应用部署的标准方式。",
                "全面讲解 Docker 容器化部署，含 Dockerfile 编写、docker-compose 编排及常用命令。",
                "https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800",
                "运维部署", "Docker,容器,DevOps", zhangwei, 634, 22);

        Article a5 = createArticle("Git 工作流最佳实践",
                "## Git Flow 分支模型\n\n- **main**: 生产环境分支\n- **develop**: 开发集成分支\n- **feature/**: 新功能分支\n- **release/**: 发布准备分支\n- **hotfix/**: 紧急修复分支\n\n## Commit 规范（Conventional Commits）\n\n```\nfeat: 新增用户登录功能\nfix: 修复评论分页Bug\ndocs: 更新API文档\nrefactor: 重构评论模块\nperf: 优化文章列表查询\ntest: 增加单元测试\nchore: 更新依赖版本\n```\n\n## 日常工作流\n\n```bash\n# 创建功能分支\ngit checkout -b feature/user-login develop\n\n# 开发完成后合并\ngit checkout develop\ngit merge --no-ff feature/user-login\ngit branch -d feature/user-login\n\n# 发布准备\ngit checkout -b release/1.0.0 develop\n```\n\n## 总结\n\n规范的 Git 工作流是团队高效协作的基础。",
                "介绍 Git Flow 工作流策略、Conventional Commits 规范和实际开发中的最佳实践。",
                "https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800",
                "开发工具", "Git,版本控制,协作", zhangwei, 412, 16);

        Article a6 = createArticle("Redis 缓存在 Spring Boot 中的应用",
                "## 集成 Redis\n\n```xml\n<dependency>\n    <groupId>org.springframework.boot</groupId>\n    <artifactId>spring-boot-starter-data-redis</artifactId>\n</dependency>\n```\n\n## 配置\n\n```yaml\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    lettuce:\n      pool:\n        max-active: 8\n```\n\n## 注解驱动缓存\n\n```java\n@Service\npublic class ArticleService {\n\n    @Cacheable(value = \"articles\", key = \"#id\")\n    public Article getById(Long id) {\n        return articleRepository.findById(id).orElse(null);\n    }\n\n    @CacheEvict(value = \"articles\", key = \"#article.id\")\n    public Article update(Article article) {\n        return articleRepository.save(article);\n    }\n}\n```\n\n## 分布式锁\n\n```java\nString lockKey = \"lock:order:\" + orderId;\nBoolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, \"1\", 30, TimeUnit.SECONDS);\nif (!acquired) throw new RuntimeException(\"请勿重复操作\");\n```\n\n## 总结\n\n合理使用 Redis 缓存可以显著提升系统读性能，减少数据库压力。",
                "讲解 Redis 缓存在 Spring Boot 中的集成方式，含注解缓存、分布式锁实战案例。",
                "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800",
                "Java技术", "Redis,缓存,Spring Boot", wangfang, 489, 17);

        Article a7 = createArticle("动态规划入门到精通",
                "## DP 核心思路\n\n1. **定义状态**: dp[i] 表示什么\n2. **状态转移方程**: dp[i] = f(dp[i-1], ...)\n3. **初始化边界**\n4. **遍历顺序**\n\n## 经典例题\n\n### 爬楼梯（Fibonacci）\n\n```java\npublic int climbStairs(int n) {\n    if (n <= 2) return n;\n    int a = 1, b = 2;\n    for (int i = 3; i <= n; i++) {\n        int c = a + b; a = b; b = c;\n    }\n    return b;\n}\n```\n\n### 0/1 背包\n\n```java\nfor (int i = 1; i <= n; i++) {\n    for (int j = W; j >= w[i]; j--) {\n        dp[j] = Math.max(dp[j], dp[j - w[i]] + v[i]);\n    }\n}\n```\n\n### 最长递增子序列（LIS）\n\n```java\nint[] dp = new int[n];\nArrays.fill(dp, 1);\nfor (int i = 1; i < n; i++)\n    for (int j = 0; j < i; j++)\n        if (nums[j] < nums[i])\n            dp[i] = Math.max(dp[i], dp[j] + 1);\n```\n\n## 总结\n\n动态规划是算法面试高频考点，多练多总结规律是关键。",
                "从入门到精通讲解动态规划，含爬楼梯、背包问题、LIS 的完整解析与代码实现。",
                "https://images.unsplash.com/photo-1509228627152-72ae9ae6848d?w=800",
                "算法", "算法,动态规划,LeetCode", lihua, 678, 31);

        Article a8 = createArticle("个人博客系统毕业设计总结",
                "## 项目背景\n\n本项目是基于 Java + Vue 开发的个人博客系统，采用前后端分离 B/S 架构。\n\n## 技术选型\n\n| 层次 | 技术 |\n|------|------|\n| 前端 | Vue 3 + Element Plus + Pinia |\n| 后端 | Spring Boot 3.x + Spring Security |\n| 数据库 | H2（开发）/ MySQL 8（生产）|\n| AI | OpenAI 兼容接口 |\n\n## 核心功能\n\n1. **用户系统**: 注册、登录（JWT）、个人资料管理\n2. **文章系统**: 发布（Markdown）、管理（CRUD）、分类标签\n3. **评论系统**: 评论互动 + **AI 智能审查**\n4. **AI 功能**: 博客详情 **AI 自动摘要**\n5. **管理后台**: 用户/文章/评论/配置 全面管理\n\n## AI 功能亮点\n\n- 评论提交时自动调用 AI 审查，过滤不规范内容\n- 博客详情页支持一键 AI 总结，提升阅读效率\n\n## 数据库设计\n\n核心表：users、articles、comments、system_config\n\n## 总结\n\n本系统实现了完整的功能闭环，融合 AI 技术是最大亮点。",
                "个人博客系统毕业设计总结，涵盖技术选型、核心功能、AI集成及设计亮点。",
                "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800",
                "项目总结", "博客系统,Java,Vue,AI,毕业设计", chenxiao, 345, 15);

        // 创建评论
        createComment("写得很详细，Spring Boot 3.x 的新特性讲解得非常清楚，感谢分享！", a1, lihua, null);
        createComment("请问 Spring Boot 3.x 和 2.x 在配置上有什么主要区别？", a1, wangfang, null);
        createComment("主要区别是需要 JDK 17+，包名从 javax 改成了 jakarta，很多配置类也有变化。", a1, zhangwei, 2L);
        createComment("Composition API 真的比 Options API 好用太多了，代码组织更清晰！", a2, wangfang, null);
        createComment("script setup 语法糖真的香，一下子少写很多代码。", a2, chenxiao, null);
        createComment("索引设计那部分讲得很好，联合索引最左前缀原则确实容易踩坑。", a3, zhangwei, null);
        createComment("请问 EXPLAIN 里 Using filesort 是什么意思？", a3, chenxiao, null);
        createComment("Using filesort 说明排序没有走索引，可以给 ORDER BY 的字段加索引优化。", a3, wangfang, 7L);
        createComment("Docker 这篇帮助很大，终于搞清楚 image 和 container 的区别了！", a4, lihua, null);
        createComment("Redis 缓存注解方式真方便，不用手动操作 RedisTemplate 了。", a6, chenxiao, null);
        createComment("动态规划一直是我的弱点，这篇讲得通俗易懂，收藏了！", a7, lihua, null);
        createComment("这个博客系统的 AI 功能设计很亮眼，评论审查和文章总结都很实用！", a8, zhangwei, null);

        log.info("演示数据初始化完成！管理员账号: admin / admin123");
    }

    private User createUser(String username, String pwd, String email, String role, String avatar, String bio) {
        User u = new User();
        u.setUsername(username); u.setPassword(pwd); u.setEmail(email);
        u.setRole(role); u.setAvatar(avatar); u.setBio(bio);
        u.setStatus(1);
        return userRepository.save(u);
    }

    private void saveConfig(String key, String value, String desc) {
        SystemConfig c = new SystemConfig();
        c.setConfigKey(key); c.setConfigValue(value); c.setDescription(desc);
        configRepository.save(c);
    }

    private Article createArticle(String title, String content, String summary,
                                   String cover, String category, String tags,
                                   User author, int views, int likes) {
        Article a = new Article();
        a.setTitle(title); a.setContent(content); a.setSummary(summary);
        a.setCoverImage(cover); a.setCategory(category); a.setTags(tags);
        a.setAuthor(author); a.setStatus(1);
        a.setViewCount(views); a.setLikeCount(likes);
        return articleRepository.save(a);
    }

    private void createComment(String content, Article article, User author, Long parentId) {
        Comment c = new Comment();
        c.setContent(content); c.setArticle(article); c.setAuthor(author);
        c.setParentId(parentId); c.setStatus(1);
        c.setAiReviewResult(1); c.setAiReviewReason("演示数据，默认通过");
        commentRepository.save(c);
    }
}
