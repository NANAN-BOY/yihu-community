package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface ActivityService {


    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件实体信息
     */
    int uploadFile(MultipartFile file, Integer activityId,Integer sort,Integer projectId) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException;

    int insertnews(Integer activityId, String platform, String link);


    void deleteFilesByActivityId(Integer activityId);

    void deleteNewsByActivityId(Integer activityId);

    void physicalDeleteNewsByActivityId(Integer activityId);

    void deleteActivityById(Integer id,Integer userId,Integer projectId);

    int addActivity(Integer projectId,Integer questionnaireId,Integer userId);

    void deleteFile(Integer fileId);

    void update(ActivityDTO activityDTO, Integer userId);

    ActivityFiles getFileById(Integer id);

    PageInfo<Activity> getActivityByProjectId(Integer projectId, String title,int pageNum, int pageSize);

    PageInfo<Activity> queryAllSubmited(String title, int pageNum, int pageSize);

    ActivityFiles getById(int newId);

    Activity getActivityById(Integer activityId);

    List<ActivityFiles> getFilesByActivityId(Integer activityId);

    List<ActivityNews> getNewsByActivityId(Integer id);

    void updateStatus(Integer activityId,Integer status,Integer userId,String reason);


    List<Activity> getActivityIdByProjectId(Integer projectId);

    PageInfo<ActivityAuditDTO> getActivityAuditList(ActivityAuditDTO activityAuditDTO, int pageNum, int pageSize);

    Activity getActivityByQuestionnaireId(Integer questionnaireId);

    String generateSignedUrl(String fileUrl);

    String packageAndDownloadActivityFiles(Integer activityId) throws IOException;
}
