package com.korzhov.todo.service;

import com.korzhov.todo.enumeration.file.ImageContentType;
import com.korzhov.todo.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;

@Service
@Slf4j
public class FileStorageService {

  private final Path root = Paths.get("uploads");

  @PostConstruct
  public void init() {
    try {
      if (!root.toFile().exists()) {
        Files.createDirectory(root);
        log.info("Directory created");
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  public String getEncodedImage(String fileName) {
    if (Objects.nonNull(fileName)) {
      InputStream in = null;
      try {
        Path file = root.resolve(fileName);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
          in = resource.getInputStream();
          return Base64.getEncoder().encodeToString(IOUtils.toByteArray(in));
        } else {
          throw new ResourceNotFoundException("Could not read the file!");
        }
      } catch (IOException e) {
        throw new ResourceNotFoundException("Failed to find or load image file");
      } finally {
        IOUtils.closeQuietly(in);
      }
    }
    return null;
  }

  public String save(MultipartFile file) {
    try {
      if (file.getOriginalFilename() != null) {
        log.info("Saving file = {}", file.getOriginalFilename());
        String fileName = resolveJpgFileName(
            UUID.randomUUID().toString(), file.getContentType());
        Files.copy(file.getInputStream(), this.root.resolve(fileName));
        return fileName;
      } else
        throw new IllegalArgumentException("File is NULL");
    } catch (IOException e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  public void deleteIfExists(String fileName) {
    try {
      if (Objects.nonNull(fileName)) {
        Path filePath = root.resolve(fileName);
        log.info("Deleting file.");
        Files.delete(filePath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String resolveJpgFileName(String fileName, String contentType) {
    ImageContentType ict = ImageContentType.fromContentType(contentType);
    switch (ict) {
      case JPG:
        return String.format("%s.%s", fileName, ImageContentType.JPG.getFileFormat());
      case PNG:
        return String.format("%s.%s", fileName, ImageContentType.PNG.getFileFormat());
      case WEBP:
        return String.format("%s.%s", fileName, ImageContentType.WEBP.getFileFormat());
      default:
        throw new IllegalArgumentException("Unknown image format");
    }
  }

}
