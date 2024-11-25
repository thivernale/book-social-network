package org.thivernale.booknetwork.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private static final String USERS_UPLOAD_DIR = "users";

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile file, @Nonnull Long userId) {
        String fileUploadSubPath = String.join(separator, USERS_UPLOAD_DIR, userId.toString());
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile file, @Nonnull String fileUploadSubPath) {
        File targetFolder = new File(String.join(separator, fileUploadPath, fileUploadSubPath));
        if (!targetFolder.exists()) {
            if (!targetFolder.mkdirs()) {
                log.warn("Failed to create folder {}", targetFolder.getAbsolutePath());
                return null;
            }
        }

        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = String.join(separator, targetFolder.getPath(),
            System.currentTimeMillis() + "." + fileExtension);
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, file.getBytes());
            log.info("File saved to {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return "";
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > -1 ? filename.substring(lastDotIndex + 1)
            .toLowerCase() : "";
    }
}
