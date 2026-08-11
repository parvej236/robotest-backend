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

    private static final long MAX_SIZE = 500 * 1024 * 1024; // 500 MB
    private static final List<String> ALLOWED_IMAGE_TYPES =
            List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    // ── Store any file in a subfolder ─────────────────────────
    public String storeFile(MultipartFile file, String subfolder) {
        validateNotEmpty(file);
        if (file.getSize() > MAX_SIZE)
            throw AppException.badRequest("File exceeds 500 MB limit");
        return store(file, subfolder);
    }

    // ── Store image with validation ───────────────────────────
    public String storeProfileImage(MultipartFile file, String subfolder) {
        validateNotEmpty(file);
        if (file.getSize() > MAX_SIZE)
            throw AppException.badRequest("File exceeds 500 MB limit");
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType()))
            throw AppException.badRequest("Allowed image types: JPEG, PNG, WebP, GIF");
        return store(file, subfolder);
    }

    // Convenience overload for profile avatars
    public String storeProfileImage(MultipartFile file) {
        return storeProfileImage(file, "profiles");
    }

    // ── Delete file by URL path ───────────────────────────────
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            // fileUrl = "/uploads/profiles/x.jpg"  →  strip leading "/"
            String rel = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            // strip the leading "uploads/" since uploadDir already is "uploads"
            if (rel.startsWith("uploads/")) rel = rel.substring("uploads/".length());
            Path path = Paths.get(uploadDir).toAbsolutePath().resolve(rel);
            boolean deleted = Files.deleteIfExists(path);
            log.info("File delete {}: {}", path, deleted ? "OK" : "not found");
        } catch (IOException e) {
            log.warn("Could not delete {}: {}", fileUrl, e.getMessage());
        }
    }

    // ── Private helpers ───────────────────────────────────────
    private String store(MultipartFile file, String subfolder) {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().resolve(subfolder);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.')) : "";
            String filename = UUID.randomUUID() + ext;

            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + subfolder + "/" + filename;
            log.info("Stored: {}", target);
            return url;

        } catch (IOException e) {
            throw AppException.badRequest("Failed to store file: " + e.getMessage());
        }
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw AppException.badRequest("File is empty");
    }
}



//package com.robotest.service;
//
//import com.robotest.exception.AppException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class FileStorageService {
//
//    @Value("${app.upload.dir:uploads}")
//    private String uploadDir;
//
//    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
//    private static final List<String> ALLOWED_TYPES =
//            List.of("image/jpeg", "image/png", "image/webp", "image/gif");
//
//    public String storeProfileImage(MultipartFile file) {
//        if (file == null || file.isEmpty())
//            throw AppException.badRequest("File is empty");
//        if (file.getSize() > MAX_SIZE)
//            throw AppException.badRequest("File exceeds 5 MB limit");
//        if (!ALLOWED_TYPES.contains(file.getContentType()))
//            throw AppException.badRequest("Allowed: JPEG, PNG, WebP, GIF");
//
//        try {
//            // Use absolute path — same base that StaticResourceConfig uses
//            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath();
//            Path profilesDir = uploadRoot.resolve("profiles");
//            Files.createDirectories(profilesDir);
//
//            String original = file.getOriginalFilename();
//            String ext = (original != null && original.contains("."))
//                    ? original.substring(original.lastIndexOf('.'))
//                    : ".jpg";
//            String filename = UUID.randomUUID() + ext;
//
//            Path target = profilesDir.resolve(filename);
//            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//
//            // Log so you can verify the saved path
//            log.info("Avatar saved to: {}", target.toAbsolutePath());
//
//            // Return relative URL path for storing in DB
//            return "/uploads/profiles/" + filename;
//
//        } catch (IOException e) {
//            throw AppException.badRequest("Could not store file: " + e.getMessage());
//        }
//    }
//
//    public void delete(String fileUrl) {
//        if (fileUrl == null || fileUrl.isBlank()) return;
//        try {
//            String rel = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
//            Path path = Paths.get(uploadDir).toAbsolutePath().resolve(
//                    rel.replace("uploads/", "")
//            );
//            boolean deleted = Files.deleteIfExists(path);
//            log.info("Delete {}: {}", path, deleted ? "success" : "not found");
//        } catch (IOException e) {
//            log.warn("Could not delete {}: {}", fileUrl, e.getMessage());
//        }
//    }
//}