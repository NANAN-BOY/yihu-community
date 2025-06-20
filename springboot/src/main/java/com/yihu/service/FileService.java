package com.yihu.service;

import cn.hutool.core.io.resource.Resource;
import com.yihu.dto.UserUpdateDTO;
import com.yihu.entity.File;
import com.yihu.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileService{

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件实体信息
     */
    int uploadFile(MultipartFile file) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException;


    File getFileInfoById(Integer id);

    String generateSingnedUrl(String storagePath);
}
