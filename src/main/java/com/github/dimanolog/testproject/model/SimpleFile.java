package com.github.dimanolog.testproject.model;

import javax.validation.constraints.NotNull;
import java.io.File;

public class SimpleFile {

    public static SimpleFile of(File file) {
        SimpleFile simpleFile = new SimpleFile();
        simpleFile.fileName = file.getName();
        simpleFile.size = file.length();

        return simpleFile;
    }

    @NotNull
    private String fileName;
    private String contentType;
    private long size;
    private String fileDownloadUri;

    private SimpleFile() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public static final class Builder {
        private long size;
        private String fileName;
        private String contentType;
        private String fileDownloadUri;

        private Builder() {
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder withSize(long size) {
            this.size = size;
            return this;
        }

        public Builder withFileDownloadUri(String fileDownloadUri) {
            this.fileDownloadUri = fileDownloadUri;
            return this;
        }

        public SimpleFile build() {
            SimpleFile simple = new SimpleFile();
            simple.size = size;
            simple.fileName = fileName;
            simple.contentType = contentType;
            simple.fileDownloadUri = fileDownloadUri;

            return simple;
        }
    }
}
