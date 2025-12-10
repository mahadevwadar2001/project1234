package com.example.conversion.util;




import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtils {

    public static String cleanFilename(MultipartFile file) {
        return StringUtils.cleanPath(file.getOriginalFilename());
    }
}
