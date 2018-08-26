package com.github.dimanolog.testproject.restfileserver.services;

import com.github.dimanolog.testproject.restfileserver.FileStorageProperties;
import com.github.dimanolog.testproject.restfileserver.exceptions.FileStorageException;
import com.github.dimanolog.testproject.common.FileUtil;
import com.github.dimanolog.testproject.model.SimpleFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getFileStorageDir())
                .toAbsolutePath().normalize();
        initialize();
    }

    private void initialize() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create folder", ex);
        }
    }

    public String saveFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (!FileUtil.validateFileName(fileName)) {
            throw new FileStorageException("Filename contains invalid path sequences " + fileName);
        }

        fileName = FileUtil.generateUniqueFileName(fileName);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }


    public List<SimpleFile> getAllFileStorageFiles() {
        try {
            return Files.walk(fileStorageLocation, 1)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(SimpleFile::of)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileStorageException("Failed to read stored files", e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found " + fileName, ex);
        }
    }
}
