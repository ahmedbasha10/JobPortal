package com.logic.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileDownloadUtil {

    private Path foundFile;

    public Resource getFileAsResource(String downloadDir, String fileName) throws IOException {
        Path path = Paths.get(downloadDir);
        try(Stream<Path> files = Files.list(path)){
            files.forEach(file -> {
                if(file.getFileName().toString().startsWith(fileName)){
                    foundFile = file;
                }
            });
        }

        if(foundFile != null){
            return new UrlResource(foundFile.toUri());
        }
        return null;
    }
}
