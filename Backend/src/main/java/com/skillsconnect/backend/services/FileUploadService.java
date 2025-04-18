package com.skillsconnect.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.upload_profile}")
    private String uploadProfile;

    public String uploadFile(MultipartFile file) {
        try {
            // Ensure the directory exists
            Path path = Paths.get(uploadDir);
            System.out.println("Try uploading ");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Try creating path");
            }

            // Save the file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            System.out.println(fileName);
            Path filePath = path.resolve(fileName);
            System.out.println(filePath.toString());
            file.transferTo(filePath);
            System.out.println("Done");
            return fileName;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "Fail";
        }
    }

    public String uploadProfileImage(MultipartFile file)
    {
        try{
            Path path = Paths.get(uploadProfile);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // Save the file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = path.resolve(fileName);
            file.transferTo(filePath.toFile());

            return fileName;
        } catch (IOException e) {
            return "Fail";
        }
    }

    public File getFile(String fileName) {
        return new File(uploadDir + File.separator + fileName);
    }

    public File getProfile(String filename)
    {
        return new File(uploadProfile+File.separator+filename);
    }
}
