package com.robotest.service;

import com.robotest.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    public String storeProfileImage(MultipartFile file) {
        // Validate
        if (file == null || file.isEmpty())
            throw AppException.badRequest("File is empty");
        if (file.getSize() > MAX_SIZE)
            throw AppException.badRequest("File exceeds 5 MB limit");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw AppException.badRequest("Allowed types: JPEG, PNG, WebP, GIF");

        try {
            Path dir = Paths.get(uploadDir, "profiles");
            Files.createDirectories(dir);

            String original  = file.getOriginalFilename();
            String ext       = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.')) : "";
            String filename  = UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(),
                    dir.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profiles/" + filename;

        } catch (IOException e) {
            throw AppException.badRequest("Could not store file: " + e.getMessage());
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            String rel = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Files.deleteIfExists(Paths.get(rel));
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", fileUrl, e.getMessage());
        }
    }
}