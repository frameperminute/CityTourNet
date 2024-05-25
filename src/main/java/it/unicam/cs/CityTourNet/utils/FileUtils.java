package it.unicam.cs.CityTourNet.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileUtils {
    @Value("${photosResources.path}")
    private String photosPath;

    @Value("${videosResources.path}")
    private String videosPath;


    public ResponseEntity<Object> fileDownload(String filepath) {
        File file = new File(filepath);
        String extension = file.getName().substring(file.getName().lastIndexOf('.'));
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders header = new HttpHeaders();
            header.add("Content-disposition",String.format("attachment; filename=\"%s\"",
                    file.getName()));
            header.add("Cache-control","no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires","0");
            return ResponseEntity.ok().headers(header).contentLength(file.length())
                    .contentType(MediaType.parseMediaType(this.getMediaType(extension))).body(resource);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>("File non trovato", HttpStatus.NOT_FOUND);
        }
    }

    public String controllaFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File non trovato";
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(this.getFilePath(extension) == null) {
            return "File non supportato";
        }
        return this.getFilePath(extension) + originalFilename;
    }

    private String getMediaType(String extension) {
        return switch (extension) {
            case ".jpeg", ".jpg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".mp4" -> "video/mp4";
            default -> "";
        };
    }

    private String getFilePath(String extension) {
        switch (extension) {
            case ".jpg", ".jpeg", ".png", ".gif" -> {
                return this.photosPath;
            }
            case ".mp4" -> {
                return this.videosPath;
            }
            default -> {
                return null;
            }
        }
    }
}
