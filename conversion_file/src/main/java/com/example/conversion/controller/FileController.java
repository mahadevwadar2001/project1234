package com.example.conversion.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

@Controller
public class FileController {

    private final String UPLOAD_DIR = "uploads";

    @GetMapping("/")
    public String home(Model model) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        model.addAttribute("files",
                Files.list(uploadPath)
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList())
        );

        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please choose a file!");
            return "redirect:/";
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.copy(file.getInputStream(),
                    uploadPath.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

            redirectAttributes.addFlashAttribute("message",
                    "Uploaded: " + file.getOriginalFilename());

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Upload failed!");
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // ✅ CORRECT FILE DOWNLOAD
    @GetMapping("/download/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) throws IOException {

        Path filePath = Paths.get(UPLOAD_DIR).resolve(name).normalize();

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + name);
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(filePath)))
                .body(resource);
    }

    @PostMapping("/delete/{name}")
    public String delete(@PathVariable String name) throws IOException {
        Path file = Paths.get(UPLOAD_DIR).resolve(name);

        if (Files.exists(file)) {
            Files.delete(file);
        }

        return "redirect:/";
    }
}
