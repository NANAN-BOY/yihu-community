package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import com.yihu.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface ProjectService {



    int addProject(Integer userId, String name);

    void deleteProjectById(Integer projectId, Integer userId);

    void update(Project project, Integer userId);

    ActivityFiles getFileById(Integer id);

    PageInfo<Project> queryByCreateId(Integer createId, Project project,int pageNum, int pageSize);

    PageInfo<Project> queryAllSubmited(Project project, int pageNum, int pageSize);


    Project getProjectById(Integer projectId);


    void updateStatus(Integer projectId,Integer status, Integer userId);


    PageInfo<ActivityAuditDTO> checkActivityStatus(Integer projectId);
}
