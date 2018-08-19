package com.github.dimanolog.testproject;

import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;

public class FileUtil {
    public static final String FILE_NAME_REGEX_PATTERN = "^[\\w\\- ]+\\.?[\\w]{0,5}$";
    public static final String FILE_EXTENSION_REGEX_PATTERN = "\\.(?=[^.]+$)";

    public static boolean validateFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        return fileName.matches(FILE_NAME_REGEX_PATTERN);
    }


    public static String generateUniqueFileName(String fileName) {
        checkEmptyString(fileName);
        long currentNanoTime = System.currentTimeMillis();

        String[] fileNameArray = fileName.split(FILE_EXTENSION_REGEX_PATTERN);

        StringBuilder fileNameSb = new StringBuilder()
                .append(fileNameArray[0])
                .append("-")
                .append(currentNanoTime);

        if (fileNameArray.length == 2) {
            fileNameSb.append(".")
                    .append(fileNameArray[1]);
        }

        return fileNameSb.toString();

    }

    private static void checkEmptyString(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new InvalidParameterException("Parameter fileName can not be null or empty");
        }
    }
}
