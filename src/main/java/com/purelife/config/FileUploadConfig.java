package com.purelife.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 檔案上傳設定
 * 讓 Spring Boot 可以訪問 uploads 資料夾的圖片
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 建立 uploads 資料夾（如果不存在）
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
            System.out.println("建立圖片上傳資料夾: " + uploadFolder.getAbsolutePath());
        }

        // 設定靜態資源映射
        // /uploads/** 的請求 → 映射到實際的 uploads 資料夾
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadFolder.getAbsolutePath() + "/");
    }
}