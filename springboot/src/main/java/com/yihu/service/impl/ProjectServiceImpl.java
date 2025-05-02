package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import com.yihu.entity.Project;
import com.yihu.mapper.*;
import com.yihu.service.ActivityService;
import com.yihu.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    //@Value("${file.storage.dir}") // 从配置文件中读取文件存储目录
    private String storageDir = System.getProperty("user.dir") + "/files/";

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ActivityFilesMapper activityFilesMapper;
    @Autowired
    private ActivityNewsMapper activityNewsMapper;



    @Override
    public int addProject(Integer userId, String name) {
        Project project = new Project();
        project.setName(name);
        project.setStatus(0);
        project.setDelFlag("N");
        project.setCreateTime(new Timestamp(System.currentTimeMillis()));
        project.setCreateById(userId);
        project.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        project.setUpdateById(userId);


        projectMapper.create(project);

        return project.getId();
    }


    @Override
    public void deleteProjectById(Integer projectId, Integer userId) {
        Project project = new Project();
        project.setId(projectId);
        project.setDelFlag("Y");
        project.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        project.setUpdateById(userId);

        projectMapper.deleteById(project);
    }

    @Override
    public void update(Project project, Integer userId) {
        Project newProject = new Project();
        newProject.setId(project.getId());
        newProject.setName(project.getName());
        newProject.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        newProject.setUpdateById(userId);

        projectMapper.update(newProject);

    }

    @Override
    public ActivityFiles getFileById(Integer id) {
        return null;
    }


    @Override
    public PageInfo<Project> queryByCreateId(Integer createId, Project project,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Project  newproject = new Project();
        newproject.setCreateById(createId);
        newproject.setName(project.getName());
        newproject.setStatus(project.getStatus());
        return new PageInfo<>(projectMapper.queryByCreateId(newproject));
    }

    @Override
    public PageInfo<Project> queryAllSubmited(Project project, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Project newproject = new Project();
        newproject.setName(project.getName());
        newproject.setStatus(project.getStatus());
        return new PageInfo<>(projectMapper.queryAllSubmited(newproject));
    }


    @Override
    public Project getProjectById(Integer projectId) {

        return projectMapper.findById(projectId);
    }



    @Override
    public void updateStatus(Integer activityId,Integer status, Integer userId, String reason) {
        Project project = new Project();
        project.setId(activityId);
        project.setStatus(status);
        project.setRejectReason(reason);
        project.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        project.setUpdateById(userId);

        projectMapper.updateStatus(project);
    }

    @Override
    public PageInfo<ActivityAuditDTO> checkActivityStatus(Integer projectId) {
        return new PageInfo<>(projectMapper.checkActivityStatus(projectId));
    }


    /**
     * 保存文件到存储目录
     */
    private String saveFileToStorage(MultipartFile file,Integer activityId,Integer sort,String storageName) throws IOException {
        // 1. 获取文件后缀（如 ".jpg"）
        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));

        // 2. 根据sort决定子目录名称
        String subDir = switch (sort) {
            case 1, 2, 3 -> "签到"; // sort 为 1、2、3 时，子目录为 "签到"
            case 4 -> "活动过程";    // sort 为 4 时，子目录为 "活动过程"
            case 5 -> "纸质新闻稿";  // sort 为 5 时，子目录为 "纸质新闻稿"
            case 6 -> "附件";       // sort 为 6 时，子目录为 "附件"
            default -> throw new IllegalArgumentException("非法的sort值: " + sort);
        };


        // 3. 构建完整存储路径（格式：根目录/活动id/子目录/）
        Path targetDir = Paths.get(storageDir, activityId.toString(), subDir);
        Files.createDirectories(targetDir); // 自动创建多级目录

        // 4. 唯一文件名
        Path targetPath = targetDir.resolve(storageName);


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
        // 5. 保存文件
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件保存失败: {}", targetPath, e);
            throw new RuntimeException("文件保存失败", e);
        }

        return targetPath.toString();
    }
}
