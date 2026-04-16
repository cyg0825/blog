package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件上传路径配置
        String configuredPath = System.getProperty("file.upload.path");
        String filePath = configuredPath != null ? configuredPath : System.getProperty("user.dir") + java.io.File.separator + "files";
        
        // 映射文件访问路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + filePath + "/");
    }
}