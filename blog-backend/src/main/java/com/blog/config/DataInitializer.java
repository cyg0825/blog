package com.blog.config;

import com.blog.entity.*;
import com.blog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
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
        User cc = createUser("cc", pwd, "cc@example.com", "ADMIN",
                "https://th.bing.com/th/id/OIP.MGZJpcXbYBa7RxT_1Ol_mgHaHa?r=0&o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3",
                "全栈开发工程师，系统管理员，负责平台日常运营与维护。");

        User admin = createUser("admin", pwd, "admin@blog.com", "ADMIN",
                "https://n.sinaimg.cn/sinakd20114/580/w690h690/20220130/31c9-879cda5e7ab5b134f96c4cc4988786ea.jpg",
                "系统管理员，负责平台日常运营与维护。");

        // 创建系统配置
        saveConfig("site_name", "技术博客平台", "网站名称");
        saveConfig("site_description", "分享技术，记录生活，连接开发者", "网站描述");
        saveConfig("ai_review_enabled", "true", "是否开启AI评论审查");
        saveConfig("ai_summary_enabled", "true", "是否开启AI文章总结");
        saveConfig("allow_register", "true", "是否允许注册");
        saveConfig("footer_text", "© 2026 技术博客平台. All Rights Reserved.", "页脚文本");
        saveConfig("max_comment_length", "1000", "评论最大字符数");

        log.info("演示数据初始化完成！管理员账号: cc / admin123, admin / admin123");
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
}