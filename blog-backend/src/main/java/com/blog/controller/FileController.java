package com.blog.controller;

import cn.hutool.core.io.FileUtil;
import com.blog.dto.ApiResponse;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    @Value("${file.upload.path:#{null}}")
    private String filePathConfig;
    
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    
    // 默认文件存储路径
    private static final String DEFAULT_FILE_PATH = System.getProperty("user.dir") + File.separator + "files";
    
    // 支持的图片扩展名集合
    private static final Set<String> IMAGE_EXTENSIONS;
    
    static {
        Set<String> exts = new HashSet<>();
        exts.add("jpg");
        exts.add("jpeg");
        exts.add("png");
        exts.add("gif");
        exts.add("bmp");
        exts.add("webp");
        IMAGE_EXTENSIONS = Collections.unmodifiableSet(exts);
    }
    
    /**
     * 头像上传接口
     */
    @PostMapping("/avatar/upload")
    public ApiResponse<String> uploadAvatar(@RequestHeader("Authorization") String authHeader, 
                                           @RequestParam("file") MultipartFile file) throws IOException {
        String token = extractTokenFromHeader(authHeader);
        String username = jwtUtils.getUsernameFromToken(token);
        
        if (file.isEmpty()) {
            return ApiResponse.fail("请选择文件");
        }
        
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ApiResponse.fail("文件名不能为空");
        }
        
        String extName = FileUtil.extName(originalFilename).toLowerCase();
        if (!isImageType(extName)) {
            return ApiResponse.fail("只支持图片格式: jpg, jpeg, png, gif, bmp, webp");
        }
        
        // 限制文件大小 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ApiResponse.fail("文件大小不能超过5MB");
        }
        
        String actualFilePath = filePathConfig != null ? filePathConfig : DEFAULT_FILE_PATH;
        File parentFile = new File(actualFilePath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        // 生成唯一的文件名
        String fileName = "avatar_" + username + "_" + System.currentTimeMillis() + "." + extName;
        File saveFile = new File(actualFilePath + File.separator + fileName);
        
        // 如果是图片，进行压缩处理
        if (isImageType(extName)) {
            compressAndSaveImage(file, saveFile, extName);
        } else {
            file.transferTo(saveFile);
        }
        
        // 更新用户头像信息
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 删除旧头像文件
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String oldAvatarPath = actualFilePath + File.separator + extractFileNameFromUrl(user.getAvatar());
                File oldFile = new File(oldAvatarPath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            user.setAvatar("/api/files/avatar/view/" + fileName);
            userRepository.save(user);
        }
        
        return ApiResponse.ok("头像上传成功", "/api/files/avatar/view/" + fileName);
    }
    
    /**
     * 从Authorization头部提取JWT令牌
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
    
    /**
     * 获取用户头像
     */
    @GetMapping("/avatar/view/{fileName:.+}")
    public void viewAvatar(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String actualFilePath = filePathConfig != null ? filePathConfig : DEFAULT_FILE_PATH;
        String fullPath = actualFilePath + File.separator + fileName;
        File file = new File(fullPath);
        
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 设置响应头
        String extName = FileUtil.extName(fileName).toLowerCase();
        String contentType = getContentTypeByExtension(extName);
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());
        response.setHeader("Cache-Control", "public, max-age=31536000");
        
        // 将文件内容写入响应
        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
    
    /**
     * 判断是否为图片类型
     */
    private boolean isImageType(String extName) {
        return extName != null && IMAGE_EXTENSIONS.contains(extName.toLowerCase());
    }
    
    /**
     * 压缩并保存图片
     */
    private void compressAndSaveImage(MultipartFile file, File saveFile, String extName) throws IOException {
        if (file.getSize() > 200 * 1024) { // 如果图片大于200KB，进行压缩
            try (var is = file.getInputStream()) {
                Thumbnails.of(is)
                    .scale(1.0)  // 保持原尺寸
                    .outputQuality(0.8)  // 压缩质量
                    .outputFormat(extName.equals("png") ? "png" : "jpg")
                    .toFile(saveFile);
            } catch (Exception e) {
                // 压缩失败则保存原图
                file.transferTo(saveFile);
            }
        } else {
            file.transferTo(saveFile);
        }
    }
    
    /**
     * 根据文件扩展名获取对应的Content-Type
     */
    private String getContentTypeByExtension(String extension) {
        Map<String, String> contentTypeMap = new HashMap<>();
        // 图片类型
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("bmp", "image/bmp");
        contentTypeMap.put("webp", "image/webp");
        
        return contentTypeMap.getOrDefault(extension, "application/octet-stream");
    }
    
    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null) return null;
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            return url.substring(lastSlashIndex + 1);
        }
        return url;
    }
    
    /**
     * WangEditor编辑器文件上传接口
     * 专门用于处理WangEditor富文本编辑器中的图片上传请求
     * 返回符合WangEditor要求格式的JSON数据
     */
    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        // 验证用户身份（如果提供了token）
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 验证token是否有效
                if (!jwtUtils.validateToken(token)) {
                    // 即使验证失败也不影响文件上传，只是记录未认证上传
                }
            } catch (Exception e) {
                // token无效，继续处理但不关联用户
            }
        }
        
        if (file.isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("errno", 1);
            errorResult.put("message", "请选择文件");
            return errorResult;
        }
        
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("errno", 1);
            errorResult.put("message", "文件名不能为空");
            return errorResult;
        }
        
        String extName = FileUtil.extName(originalFilename).toLowerCase();
        if (!isImageType(extName)) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("errno", 1);
            errorResult.put("message", "只支持图片格式: jpg, jpeg, png, gif, bmp, webp");
            return errorResult;
        }
        
        // 限制文件大小 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("errno", 1);
            errorResult.put("message", "文件大小不能超过5MB");
            return errorResult;
        }
        
        String actualFilePath = filePathConfig != null ? filePathConfig : DEFAULT_FILE_PATH;
        File parentFile = new File(actualFilePath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        // 生成唯一的文件名
        String fileName = "wang_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extName;
        File saveFile = new File(actualFilePath + File.separator + fileName);
        
        // 如果是图片，进行压缩处理
        if (isImageType(extName)) {
            compressAndSaveImage(file, saveFile, extName);
        } else {
            file.transferTo(saveFile);
        }
        
        // 构建返回结果 - 符合WangEditor要求的格式
        Map<String, Object> successResult = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> urlMap = new HashMap<>();
        
        urlMap.put("url", "/api/files/wang/view/" + fileName);
        dataList.add(urlMap);
        
        successResult.put("errno", 0);  // 0表示成功
        successResult.put("data", dataList);
        
        return successResult;
    }
    
    /**
     * 获取WangEditor上传的文件
     */
    @GetMapping("/wang/view/{fileName:.+}")
    public void viewWangFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String actualFilePath = filePathConfig != null ? filePathConfig : DEFAULT_FILE_PATH;
        String fullPath = actualFilePath + File.separator + fileName;
        File file = new File(fullPath);
        
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 设置响应头
        String extName = FileUtil.extName(fileName).toLowerCase();
        String contentType = getContentTypeByExtension(extName);
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());
        response.setHeader("Cache-Control", "public, max-age=31536000");
        
        // 将文件内容写入响应
        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}