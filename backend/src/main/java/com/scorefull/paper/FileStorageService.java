package com.scorefull.paper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${scorefull.upload-dir}") String dir) throws IOException {
        this.uploadDir = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
    }

    public String savePdf(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
        }

        String storedName = UUID.randomUUID() + ".pdf";
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadDir.resolve(storedName));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the file");
        }
        return storedName;
    }

    public Resource load(String storedName) {
        try {
            Path path = uploadDir.resolve(storedName).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
            return resource;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read the file");
        }
    }
}