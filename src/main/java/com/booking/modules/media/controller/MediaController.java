package com.booking.modules.media.controller;

import com.booking.common.ApiResponse;
import com.booking.modules.media.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media API", description = "飯店房型多圖片上傳與靜態資源存取")
public class MediaController {

    private final FileStorageService fileStorageService;

    public MediaController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload-single")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "單張圖片檔案上傳")
    public ResponseEntity<ApiResponse<String>> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success("圖片上傳成功", url));
    }

    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "多張圖片批次上傳")
    public ResponseEntity<ApiResponse<List<String>>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = fileStorageService.storeMultipleFiles(Arrays.asList(files));
        return ResponseEntity.ok(ApiResponse.success("多組圖片上傳成功", urls));
    }

    @GetMapping("/view/{fileName:.+}")
    @Operation(summary = "檢視與下載已上傳圖片")
    public ResponseEntity<Resource> getMediaFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
