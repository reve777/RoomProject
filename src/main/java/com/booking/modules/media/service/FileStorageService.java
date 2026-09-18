package com.booking.modules.media.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path storageDirectory;

    public FileStorageService(@Value("${app.upload.dir:./uploads/room-images}") String uploadDir) {
        this.storageDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException ex) {
            log.error("無法建立檔案儲存目錄: {}", this.storageDirectory, ex);
        }
    }

    /**
     * 儲存單張圖片
     */
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("無法上傳空檔案");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.storageDirectory.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/api/media/view/" + newFileName;
        } catch (IOException ex) {
            log.error("儲存檔案失敗: {}", newFileName, ex);
            throw new RuntimeException("檔案儲存失敗，請稍後再試");
        }
    }

    /**
     * 儲存多張圖片
     */
    public List<String> storeMultipleFiles(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return fileUrls;
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileUrls.add(storeFile(file));
            }
        }
        return fileUrls;
    }

    /**
     * 讀取檔案資源
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.storageDirectory.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("找不到指定檔案: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("檔案路徑無效: " + fileName);
        }
    }

    /**
     * 刪除實體檔案
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.contains("/media/view/")) {
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                Path filePath = this.storageDirectory.resolve(fileName);
                return Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("刪除檔案失敗: {}", fileUrl, e);
        }
        return false;
    }
}
