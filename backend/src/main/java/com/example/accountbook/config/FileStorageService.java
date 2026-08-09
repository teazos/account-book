package com.example.accountbook.config;

import com.example.accountbook.common.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("初始化上传目录失败: " + uploadDir, e);
        }
    }

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BizException("请选择要上传的图片");
        if (file.getSize() > MAX_SIZE) throw new BizException("图片大小不能超过5MB");
        String ext = extension(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) throw new BizException("仅支持 jpg/png/gif/webp/bmp 格式");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) throw new BizException("仅支持上传图片文件");
        String filename = "cover_" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try {
            file.transferTo(uploadDir.resolve(filename).toFile());
        } catch (IOException e) {
            throw new BizException("图片保存失败");
        }
        return "/uploads/" + filename;
    }

    public void deleteIfUploaded(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        String name = url.substring("/uploads/".length());
        if (name.contains("/") || name.contains("\\") || name.contains("..")) return;
        Path path = uploadDir.resolve(name).normalize();
        if (!path.startsWith(uploadDir)) return;
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public String resourceLocation() {
        return "file:" + uploadDir + "/";
    }

    private String extension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
