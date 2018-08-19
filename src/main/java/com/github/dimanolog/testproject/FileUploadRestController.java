package com.github.dimanolog.testproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadRestController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadRestController.class);
    private final FileService fileService;

    @Autowired
    public FileUploadRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/uploadFile")
    public SimpleFile uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileService.saveFile(file);

        return SimpleFile.getBuilder()
                .withSize(file.getSize())
                .withContentType(file.getContentType())
                .withFileName(fileName)
                .build();
    }
}
