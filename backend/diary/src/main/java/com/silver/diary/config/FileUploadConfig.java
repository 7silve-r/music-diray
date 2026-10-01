package com.silver.diary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-url-prefix}")
    private String accessUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL 以 /uploads/ 开头 -> 映射到物理磁盘目录
        registry.addResourceHandler(accessUrlPrefix + "**")
                .addResourceLocations("file:" + uploadDir); // 注意必须加 file: 前缀
    }
}