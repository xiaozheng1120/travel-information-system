package com.travel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 通用图片上传接口
 * 上传后的图片保存在服务器 uploads 目录，通过 /uploads/** 静态映射对外访问
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    /** 图片保存目录，可在 application.yml 用 travel.upload-dir 覆盖 */
    @Value("${travel.upload-dir:D:/tools/travel-platform/uploads}")
    private String uploadDir;

    private static final List<String> ALLOW_EXT =
            Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp");

    private static final long MAX_SIZE = 10 * 1024 * 1024L;

    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "请选择要上传的图片");
            return result;
        }
        if (file.getSize() > MAX_SIZE) {
            result.put("code", 400);
            result.put("msg", "图片不能超过 10MB");
            return result;
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.lastIndexOf('.') >= 0) {
            ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        }
        if (!ALLOW_EXT.contains(ext)) {
            result.put("code", 400);
            result.put("msg", "只支持 jpg / jpeg / png / webp / gif / bmp 格式");
            return result;
        }

        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            result.put("code", 500);
            result.put("msg", "上传目录创建失败：" + uploadDir);
            return result;
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest.getAbsoluteFile());
        } catch (IOException e) {
            result.put("code", 500);
            result.put("msg", "图片保存失败：" + e.getMessage());
            return result;
        }

        // 返回可访问的绝对地址，例如 http://localhost:8080/uploads/xxx.jpg
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        result.put("code", 200);
        result.put("msg", "上传成功");
        result.put("url", url);
        return result;
    }
}
