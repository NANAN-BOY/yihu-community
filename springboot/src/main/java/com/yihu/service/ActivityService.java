package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public interface ActivityService {

    int create(ActivityDTO activityDTO);

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

    int addActivity(Integer userId);

    void deleteFile(Integer fileId);

    void update(ActivityDTO activityDTO, Integer userId);

    PageInfo<Activity> queryByCreateId(Integer createId, ActivityDTO activityDTO,int pageNum, int pageSize);

    void submitActivity(ActivityDTO activityDTO, Integer id);

    ActivityFiles getById(int newId);
}
