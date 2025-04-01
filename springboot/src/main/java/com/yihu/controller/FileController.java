package com.yihu.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.entity.File;
import com.yihu.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /*
    * 前端进行文件名校验，不能是空文件名
    * */

    /**
     * 文件上传接口
     */
    @AuthAccess
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            int newId = fileService.uploadFile(file);
            if (newId != 0){
                return Result.success(newId);
            }else {
                return Result.error("文件上传失败");
            }
        } catch (Exception e) {
            return Result.error("文件上传失败");
        }
    }

    /**
     * 文件下载接口
     */
    @AuthAccess
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Integer id) {
        try {
            // 获取文件信息
            File fileEntity = fileService.getFileInfoById(id);
            if (fileEntity == null) {
                log.error("File not found: {}", id);
                return ResponseEntity.notFound().build();

            }

            // 获取文件路径
            java.io.File file = new java.io.File(fileEntity.getStoragePath());
            if (!file.exists()) {
                log.error("File not found at path: {}", fileEntity.getStoragePath());
                return ResponseEntity.notFound().build();
            }

            // 创建 InputStreamResource
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // 解析文件类型，如果无法解析则使用默认类型
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(fileEntity.getFileType());
            } catch (Exception e) {
                log.warn("Failed to parse file type: {}, using default type", fileEntity.getFileType());
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            // 返回文件下载响应
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileEntity.getName(), StandardCharsets.UTF_8.toString()) + "\"")
                    .contentType(mediaType)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @AuthAccess
    @GetMapping("/getFileStoragePath/{id}")
    public Result getFileStoragePath(@PathVariable Integer id) {
        File fileEntity = fileService.getFileInfoById(id);
        if(fileEntity != null){
            return Result.success(fileEntity.getStoragePath());
        }
        else {
            return Result.error("文件不存在");
        }
    }

}






