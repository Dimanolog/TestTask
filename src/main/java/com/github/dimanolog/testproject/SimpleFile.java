package com.github.dimanolog.testproject;

public class SimpleFile {
    private String fileName;
    private String contentType;
    private long size;

    public static Builder getBuilder(){
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

        public SimpleFile build() {
            SimpleFile simple = new SimpleFile();
            simple.size = size;
            simple.fileName = fileName;
            simple.contentType = contentType;

            return simple;
        }
    }
}
