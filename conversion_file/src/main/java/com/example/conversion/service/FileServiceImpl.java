package com.example.conversion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final Path root;

    public FileServiceImpl(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }

    @Override
    public boolean storeFile(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> listFiles() {
        try {
            return Files.list(root)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Resource downloadFile(String filename) {
        return new FileSystemResource(root.resolve(filename).toFile());
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Files.deleteIfExists(root.resolve(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
