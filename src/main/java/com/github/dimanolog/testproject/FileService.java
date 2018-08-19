package com.github.dimanolog.testproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
        if(!FileUtil.validateFileName(fileName)){
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
}
