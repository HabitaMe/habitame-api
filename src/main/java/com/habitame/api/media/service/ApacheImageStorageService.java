package com.habitame.api.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApacheImageStorageService implements ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        Path folderPath = Paths.get(uploadDir, folder);
        Files.createDirectories(folderPath);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = folderPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + folder + "/" + fileName;
    }

    @Override
    public void delete(String path) throws IOException {
        Path filePath = Paths.get(uploadDir, path.replace("/uploads/", ""));
        Files.deleteIfExists(filePath);
    }
}
