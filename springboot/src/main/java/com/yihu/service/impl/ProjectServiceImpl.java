package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
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
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private OSS ossClient;
    @Value("${aliyun.oss.bucket-name}") private String bucketName;




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

    @Override
    public String packageAndDownloadActivityFiles(Integer projectId) throws IOException {

        List<Activity> activities = activityMapper.findByProjectId(projectId);
        Project project = projectMapper.findById(projectId);

        // 创建临时目录
        Path tempDir = Files.createTempDirectory("project_download_");

        try {
            // 创建压缩包
            Path zipPath = tempDir.resolve(generateZipFileName(project));

            // 关键修改：在循环外打开ZIP输出流（追加模式）
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                for(Activity activity : activities){
                    List<ActivityFiles> fileDetails = activityFilesMapper.findByActivityId(activity.getId());
                    if (fileDetails.isEmpty()) continue;

                    // 直接使用原方法逻辑，但传入统一的zos
                    for (ActivityFiles file : fileDetails) {
                        addFileToZip(activity, file, zos); // 复用原有方法
                    }
                }
            }
            // 上传到OSS
            String ossKey = "downloads/" + zipPath.getFileName().toString();
            uploadToOSS(zipPath, ossKey);
            // 生成签名URL
            return generateSignedUrl(ossKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 6. 清理临时文件
            FileUtils.deleteDirectory(tempDir.toFile());
        }


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

    private String saveFileToStorage(MultipartFile file,Integer activityId,Integer sort,String storageName,Integer projectId) throws IOException {
        // 1. 获取文件后缀（如 ".jpg"）
        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));

        /// 2. 根据sort确定子目录
        String subDir = switch (sort) {
            case 1, 2, 3 -> "签到";
            case 4 -> "活动过程";
            case 5 -> "纸质新闻稿";
            case 6 -> "附件";
            default -> throw new IllegalArgumentException("非法的sort值: " + sort);
        };

        // 3. 构建OSS对象键（模拟目录结构）
        String objectKey = String.format("%s/%d/%d/%s/%s",
                storageDir,
                projectId,
                activityId,
                subDir,
                storageName);

        // 4. 上传到OSS
        try (InputStream is = file.getInputStream()) {
            ossClient.putObject(bucketName, objectKey, is);
        } catch (Exception e) {
            log.error("OSS文件上传失败: {}", objectKey, e);
            throw new RuntimeException("文件上传失败", e);
        }

        // 5. 返回文件OSS路径
        return objectKey;
    }


    public String generateSignedUrl(String fileUrl) {
        // 设置URL过期时间为10分钟后
        Date expiration = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

        // 生成签名URL
        String signedUrl = ossClient.generatePresignedUrl(
                bucketName,
                fileUrl,
                expiration
        ).toString();
        return signedUrl;
    }
    private String generateZipFileName(Project project) {
        return String.format("project_%s_%s.zip",
                project.getName(),
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }

    private void createActivityZipFile(Activity activity, List<ActivityFiles> files, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (ActivityFiles file : files) {
                addFileToZip(activity, file, zos);
            }
        }
    }


    private void addFileToZip(Activity activity, ActivityFiles file, ZipOutputStream zos) throws IOException {
        // 1. 下载文件到临时存储
        Path tempFile = downloadFromOSS(file.getFileUrl());

        try {
            // 2. 创建ZIP条目
            String entryName = String.format("%s/%s/%s",
                    activity.getTitle(),
                    file.getFileSort(),
                    file.getName());

            zos.putNextEntry(new ZipEntry(entryName));

            // 3. 写入文件内容
            Files.copy(tempFile, zos);

            zos.closeEntry();
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
    private Path downloadFromOSS(String ossKey) throws IOException {
        Path tempFile = Files.createTempFile("oss_temp_", "");

        try {
            OSSObject ossObject = ossClient.getObject(bucketName, ossKey);
            try (InputStream is = ossObject.getObjectContent()) {
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } catch (Exception e) {
            Files.deleteIfExists(tempFile);
            throw new IOException("下载OSS文件失败: " + ossKey, e);
        }
    }

    private void uploadToOSS(Path filePath, String ossKey) throws IOException {
        try (InputStream is = Files.newInputStream(filePath)) {
            ossClient.putObject(bucketName, ossKey, is);
        } catch (Exception e) {
            throw new IOException("上传文件到OSS失败", e);
        }
    }


}
