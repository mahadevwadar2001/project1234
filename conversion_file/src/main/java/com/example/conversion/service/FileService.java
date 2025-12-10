package com.example.conversion.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    boolean storeFile(MultipartFile file);

    List<String> listFiles();

    Resource downloadFile(String filename);

    void deleteFile(String filename);
}
