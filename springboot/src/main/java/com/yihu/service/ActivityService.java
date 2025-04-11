package com.yihu.service;

import com.yihu.dto.ActivityCreateDTO;
import com.yihu.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public interface ActivityService {

    int create(ActivityCreateDTO activityCreateDTO);

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件实体信息
     */
    int uploadFile(MultipartFile file, Integer activityId,Integer sort) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException;

    int insertnews(Integer activityId, String platform, String link);


    void deleteFilesByActivityId(Integer activityId);

    void deleteNewsByActivityId(Integer activityId);

    void deleteActivityById(Integer id,String title);

    int addActivity();

    void deleteFile(Integer fileId);

    void update(ActivityCreateDTO activityDTO);
}
