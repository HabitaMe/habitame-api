package com.habitame.api.media.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ImageStorageService {
    String store(MultipartFile file, String folder) throws IOException;

    void delete(String fileName) throws IOException;
}
