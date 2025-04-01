package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.yihu.entity.File;
import com.yihu.mapper.FileMapper;
import com.yihu.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    //@Value("${file.storage.dir}") // 从配置文件中读取文件存储目录
    private String storageDir = System.getProperty("user.dir") + "/files/";

    @Autowired
    private FileMapper fileMapper;

    @Override
    public int uploadFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        // 计算文件的 MD5
        String md5 = calculateFileMD5(file);

        // 检查文件是否已存在
        File existingFile = fileMapper.findByMd5(md5);
        if (existingFile != null) {
            // 文件已存在，增加引用次数
            existingFile.setQuoteCount(existingFile.getQuoteCount() + 1);
            // 增加引用次数
            fileMapper.updateQuoteByid(existingFile);
            // 新增一条记录
            existingFile.setQuoteCount(0);
            existingFile.setName(file.getOriginalFilename());

            fileMapper.insert(existingFile);
            //返回新增数据的id
            return existingFile.getId();

        } else {
            // 文件不存在，保存到存储目录
            String storagePath = saveFileToStorage(file, md5);

            // 获取文件 MIME 类型
            String fileType = FileUtil.getMimeType(file.getOriginalFilename());

            // 创建新的文件实体
            File newFile = new File();
            newFile.setName(file.getOriginalFilename());
            newFile.setStoragePath(storagePath);
            newFile.setOriginalName(file.getOriginalFilename());
            newFile.setFileType(fileType);
            newFile.setFileSize(file.getSize());
            newFile.setMd5(md5);
            newFile.setCreateTime(new Timestamp(System.currentTimeMillis()));
            newFile.setQuoteCount(0);


            fileMapper.insert(newFile);
            //返回新增数据的id
            return newFile.getId();
        }
    }


    @Override
    public File getFileInfoById(Integer id) {
        return fileMapper.selectById(id);
    }


    /**
     * 计算文件的 MD5 值
     */
    private String calculateFileMD5(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5Bytes = digest.digest();
        return HexFormat.of().formatHex(md5Bytes);
    }

    /**
     * 保存文件到存储目录
     */
    private String saveFileToStorage(MultipartFile file, String md5) throws IOException {
        // 获取文件的后缀名（带 "."）
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        // 创建存储目录（按文件后缀名分目录）
        Path targetDir = Paths.get(storageDir, fileExtension.substring(1)); // 去掉 "." 作为目录名
        Files.createDirectories(targetDir);

        // 生成存储文件名（MD5 + 文件扩展名）
        String fileName = md5 + fileExtension;
        Path targetPath = targetDir.resolve(fileName);

        // 保留文件，大文件切片传输
//        try (InputStream is = file.getInputStream();
//             BufferedInputStream bis = new BufferedInputStream(is);
//             OutputStream os = Files.newOutputStream(targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//             BufferedOutputStream bos = new BufferedOutputStream(os)) {
//            byte[] buffer = new byte[8192]; // 8KB 缓冲区
//            int bytesRead;
//            while ((bytesRead = bis.read(buffer)) != -1) {
//                bos.write(buffer, 0, bytesRead);
//            }
//        }
        // 保存文件
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return targetPath.toString();
    }
}
