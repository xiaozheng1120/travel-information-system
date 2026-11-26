package com.travel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 上传图片的保存目录，与 UploadController 使用同一配置 */
    @Value("${travel.upload-dir:D:/tools/travel-platform/uploads}")
    private String uploadDir;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 前台所有前端路由，刷新时转发到index.html交给Vue处理
        registry.addViewController("/hotelDetail").setViewName("forward:/index.html");
        registry.addViewController("/guideDetail").setViewName("forward:/index.html");
        registry.addViewController("/guideDetail/**").setViewName("forward:/index.html");
        registry.addViewController("/spotDetail/**").setViewName("forward:/index.html");
        registry.addViewController("/home").setViewName("forward:/index.html");
        registry.addViewController("/spot").setViewName("forward:/index.html");
        registry.addViewController("/guide").setViewName("forward:/index.html");
        registry.addViewController("/hotel").setViewName("forward:/index.html");
        registry.addViewController("/ticket").setViewName("forward:/index.html");
        registry.addViewController("/order").setViewName("forward:/index.html");
        registry.addViewController("/orderDetail/**").setViewName("forward:/index.html");
        registry.addViewController("/pay").setViewName("forward:/index.html");
        registry.addViewController("/profile").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/register").setViewName("forward:/index.html");

        // 后台管理页面路由
        registry.addViewController("/admin").setViewName("forward:/index.html");
        registry.addViewController("/admin/**").setViewName("forward:/index.html");
        registry.addViewController("/admin/home").setViewName("forward:/index.html");
        registry.addViewController("/admin/guide").setViewName("forward:/index.html");
        registry.addViewController("/admin/spot").setViewName("forward:/index.html");
        registry.addViewController("/admin/hotel").setViewName("forward:/index.html");
        registry.addViewController("/admin/ticket").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把 /uploads/** 映射到磁盘目录，上传的图片刷新后依然可以访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
