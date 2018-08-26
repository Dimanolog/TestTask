package com.github.dimanolog.testproject.clientserver.services;

import com.github.dimanolog.testproject.clientserver.exceptions.ClientServerException;
import com.github.dimanolog.testproject.clientserver.resources.MultipartInputStreamFileResource;
import com.github.dimanolog.testproject.model.SimpleFile;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class RestApiClientService {

    private RestTemplate restTemplate = new RestTemplate();

    public SimpleFile uploadFile(MultipartFile multipartFile) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (multipartFile.isEmpty()) {
            throw new ClientServerException("File is empty");
        }
        MultipartInputStreamFileResource fileResource;
        try {
            fileResource = new MultipartInputStreamFileResource(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename());
        } catch (IOException e) {
            throw new ClientServerException("Can't convert file", e);
        }

        map.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                map, headers);

        try {
            ResponseEntity<SimpleFile> result = restTemplate.exchange(
                    "http://localhost:8080/uploadFile", HttpMethod.POST, requestEntity,
                    SimpleFile.class);
            return result.getBody();
        } catch (RestClientException e) {
            throw new ClientServerException("Could not upload file to server");
        }
    }

    public List<SimpleFile> getListUploadedFiles() {
        try {
            SimpleFile[] result = restTemplate.getForObject(
                    "http://localhost:8080/listUploadedFiles", SimpleFile[].class);
            return Arrays.asList(result);
        } catch (Exception ex) {
            throw new ClientServerException("Could not get list of files");
        }
    }

    public Resource loadAsResource(String filename) {
        Resource response = restTemplate.getForObject("http://localhost:8080/downloadFile/" + filename, Resource.class);

        if (response.exists() || response.isReadable()) {
            return response;
        } else {
            throw new ClientServerException("Could not read file: " + filename);
        }
    }
}
