package com.github.dimanolog.testproject.restfileserver.controllers;

import com.github.dimanolog.testproject.model.SimpleFile;
import com.github.dimanolog.testproject.restfileserver.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class FileStorageRestController {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageRestController.class);
    private final FileService fileService;

    @Autowired
    public FileStorageRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/uploadFile")
    public SimpleFile uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileService.saveFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return SimpleFile.getBuilder()
                .withSize(file.getSize())
                .withContentType(file.getContentType())
                .withFileName(fileName)
                .withFileDownloadUri(fileDownloadUri)
                .build();
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.error("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/listUploadedFiles")
    public List<SimpleFile> listUploadedFiles() {
        return fileService.getAllFileStorageFiles();
    }
}
