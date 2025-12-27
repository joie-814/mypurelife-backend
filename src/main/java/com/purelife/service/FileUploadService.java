package com.purelife.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.purelife.repository.ProductRepository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 檔案上傳服務：把前端傳來的圖片檔案安全地存到伺服器硬碟
 */
@Service
public class FileUploadService {

    // 上傳檔案的根目錄（可在 application.properties 設定）
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // 允許的圖片類型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", 
        "image/png", 
        "image/gif", 
        "image/webp"
    );

    // 最大檔案大小（5MB）
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 初始化上傳目錄：確保 uploads/products/ 一定存在，避免第一次上傳直接炸掉。
     */
    @PostConstruct
    public void init() {
        try {
            // 建立商品圖片上傳目錄
            Path productPath = Paths.get(uploadDir, "products");
            if (!Files.exists(productPath)) {
                Files.createDirectories(productPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("無法建立上傳目錄", e);
        }
    }

    /**
     * 上傳商品圖片
     * @param file 上傳的檔案
     * @return 儲存的檔案名稱
     */
    public String uploadProductImage(MultipartFile file) throws IOException {
        // 驗證檔案
        validateImageFile(file);

        // 產生唯一檔名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString() + "." + extension;

        // 儲存檔案
        Path targetPath = Paths.get(uploadDir, "products", newFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 回傳圖片存取 URL（供資料庫與前端使用）
        return "/uploads/products/" + newFileName;
    }

    /**
     * 刪除商品圖片
     * @param fileName 檔案名稱
     */
    public void deleteProductImage(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir, "products", fileName);
        Files.deleteIfExists(filePath);
    }

    /**
     * 驗證圖片檔案
     */
    private void validateImageFile(MultipartFile file) {
        // 檢查是否為空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("請選擇要上傳的檔案");
        }

        // 檢查檔案大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("檔案大小不能超過 5MB");
        }

        // 檢查檔案類型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("只支援 JPG、PNG、GIF、WebP 格式的圖片");
        }

        // 檢查檔案名稱
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new IllegalArgumentException("無效的檔案名稱");
        }
    }

    /**
     * 取得檔案副檔名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg"; // 預設
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}