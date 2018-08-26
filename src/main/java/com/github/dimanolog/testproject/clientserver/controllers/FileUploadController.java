package com.github.dimanolog.testproject.clientserver.controllers;


import com.github.dimanolog.testproject.clientserver.exceptions.ClientServerException;
import com.github.dimanolog.testproject.clientserver.services.RestApiClientService;
import com.github.dimanolog.testproject.model.SimpleFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final RestApiClientService restApiClientService;

    @Autowired
    public FileUploadController(RestApiClientService restApiClientService) {
        this.restApiClientService = restApiClientService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        addFilesAttribute(model);

        return "uploadForm";
    }

    private void addFilesAttribute(Model model) {
        model.addAttribute("files", restApiClientService.getListUploadedFiles().stream().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName()).build().toString())
                .collect(Collectors.toList()));
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        SimpleFile s = restApiClientService.uploadFile(file);

        if (s != null) {
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + s.getFileName() + "!");
        }

        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = restApiClientService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(ClientServerException.class)
    public String handleStorageFileNotFound(Model model, ClientServerException exc) {
        model.addAttribute("message", exc.getMessage());
        addFilesAttribute(model);

        return "uploadForm";
    }
}

