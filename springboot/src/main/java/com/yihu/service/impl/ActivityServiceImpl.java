package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import com.yihu.mapper.ActivityFilesMapper;
import com.yihu.mapper.ActivityMapper;
import com.yihu.mapper.ActivityNewsMapper;
import com.yihu.service.ActivityService;
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
public class ActivityServiceImpl implements ActivityService {

    //@Value("${file.storage.dir}") // 从配置文件中读取文件存储目录
    private String storageDir = "project";

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityFilesMapper activityFilesMapper;
    @Autowired
    private ActivityNewsMapper activityNewsMapper;
    @Autowired
    private OSS ossClient;
    @Value("${aliyun.oss.bucket-name}") private String bucketName;




    @Override
    public int uploadFile(MultipartFile file, Integer activityId,Integer sort,Integer projectId) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException {

        // 5. 生成唯一文件名（时间戳 + 原始文件名） 防止重名文件
        String storageName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String storagePath = saveFileToStorage(file,activityId,sort,storageName,projectId);

        // 创建新的文件实体
        ActivityFiles activityFiles = new ActivityFiles();
        activityFiles.setActivityId(activityId);
        activityFiles.setName(file.getOriginalFilename());
        activityFiles.setStorageName(storageName);
        activityFiles.setFileSort(sort);
        activityFiles.setFileUrl(storagePath);
        activityFiles.setDelFlag("N");

        activityFilesMapper.insert(activityFiles);
        //返回新增数据的id
        return activityFiles.getId();
    }




    @Override
    public int insertnews(Integer activityId, String platform, String link) {

        ActivityNews activityNews = new ActivityNews();
        activityNews.setActivityId(activityId);
        activityNews.setPlatform(platform);
        activityNews.setLink(link);
        activityNews.setDelFlag("N");
        return activityNewsMapper.insert(activityNews);
    }

    @Override
    public void deleteFilesByActivityId(Integer activityId) {
        // 删除活动文件记录
        activityFilesMapper.deleteByActivityId(activityId);
    }


    @Override
    public void deleteNewsByActivityId(Integer activityId) {
        // 删除活动新闻记录
        activityNewsMapper.deleteByActivityId(activityId);
    }

    @Override
    public void physicalDeleteNewsByActivityId(Integer activityId) {
        // 物理删除删除活动新闻记录
        activityNewsMapper.physicalDeleteByActivityId(activityId);
    }

    @Override
    public void deleteActivityById(Integer id,Integer userId,Integer projectId) {
        // 删除活动记录
        Activity activity = new Activity();
        activity.setId(id);
        activity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setUpdateById(userId);

        activityMapper.deleteById(activity);


        // 数据库全部删除完成，要删除的目录前缀
        String directoryPrefix = "project/" + projectId + "/" + id + "/";
        try {
            // 步骤1：列举目录下所有对象
            List<String> keysToDelete = new ArrayList<>();
            String nextContinuationToken = null;

            do {
                ListObjectsV2Request listRequest = new ListObjectsV2Request(bucketName)
                        .withPrefix(directoryPrefix)        // 设置目录前缀
                        .withContinuationToken(nextContinuationToken);

                ListObjectsV2Result listingResult = ossClient.listObjectsV2(listRequest);

                for (OSSObjectSummary objectSummary : listingResult.getObjectSummaries()) {
                    keysToDelete.add(objectSummary.getKey()); // 收集对象Key
                }

                nextContinuationToken = listingResult.getNextContinuationToken();
            } while (nextContinuationToken != null);

            // 步骤2：批量删除对象
            if (!keysToDelete.isEmpty()) {
                DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                        .withKeys(keysToDelete)       // 设置待删除Key列表
                        .withQuiet(true);             // 静默模式（不返回删除详情）

                ossClient.deleteObjects(deleteRequest);
                System.out.println("成功删除目录 [" + directoryPrefix + "] 下的 " + keysToDelete.size() + " 个对象");
            } else {
                System.out.println("目录 [" + directoryPrefix + "] 不存在或为空");
            }
        } catch (OSSException | ClientException e) {
            System.err.println("操作失败: " + e.getMessage());
        }


    }

    @Override
    public int addActivity(Integer projectId,Integer questionnaireId,Integer userId) {
        Activity activity = new Activity();
        activity.setProjectId(projectId);
        activity.setQuestionnaireId(questionnaireId);
        activity.setStatus(0);
        activity.setDelFlag("N");
        activity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        activity.setCreateById(userId);
        activity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setUpdateById(userId);

        activityMapper.create(activity);

        return activity.getId();
    }

    @Override
    public void deleteFile(Integer fileId) {
        // 1. 根据文件ID查询文件记录
        ActivityFiles file = activityFilesMapper.findById(fileId);
        if (file == null) {
            log.warn("文件不存在: {}", fileId);
            return;
        }

        // 2. 删除数据库中的文件记录 出错可以回退
        activityFilesMapper.deleteById(fileId);

        // 3. 删除本地或云服务器上的文件
        String filePath = file.getFileUrl();
        try {
            // 删除单个文件
            ossClient.deleteObject(bucketName, filePath);
            System.out.println("文件删除成功: " + filePath);
        } catch (Exception e) {
            System.err.println("删除文件失败: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void update(ActivityDTO activityDTO, Integer userId) {
        Activity activity = new Activity();
        activity.setId(activityDTO.getActivityId());
        activity.setTitle(activityDTO.getTitle());
        activity.setNoticeContent(activityDTO.getNoticeContent());
        activity.setStaffCount(activityDTO.getStaffCount());
        activity.setVolunteerCount(activityDTO.getVolunteerCount());
        activity.setServiceObjectCount(activityDTO.getServiceObjectCount());
        activity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setUpdateById(userId);

        activityMapper.update(activity);

    }

    @Override
    public ActivityFiles getFileById(Integer id) {
        return activityFilesMapper.findById(id);
    }

    @Override
    public PageInfo<Activity> getActivityByProjectId(Integer projectId, String title,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Activity activity = new Activity();

        activity.setProjectId(projectId);
        activity.setTitle(title);
        return new PageInfo<>(activityMapper.queryByProjectId(activity));
    }

    @Override
    public PageInfo<Activity> queryAllSubmited(String title, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Activity activity = new Activity();
        activity.setTitle(title);
        return new PageInfo<>(activityMapper.queryAllSubmited(activity));
    }


    @Override
    public ActivityFiles getById(int newId) {
        return activityFilesMapper.findById(newId);
    }

    @Override
    public Activity getActivityById(Integer id) {
        return activityMapper.findById(id);
    }

    @Override
    public List<ActivityFiles> getFilesByActivityId(Integer activityId) {
        return activityFilesMapper.findByActivityId(activityId);
    }

    @Override
    public List<ActivityNews> getNewsByActivityId(Integer activityId) {
        return activityNewsMapper.findByActivityId(activityId);
    }

    @Override
    public void updateStatus(Integer activityId,Integer status, Integer userId,String reason) {
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setStatus(status);
        activity.setRejectReason(reason);
        activity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setUpdateById(userId);

        activityMapper.updateStatus(activity);
    }

    @Override
    public List<Activity> getActivityIdByProjectId(Integer projectId) {
        return activityMapper.findByProjectId(projectId);
    }

    @Override
    public PageInfo<ActivityAuditDTO> getActivityAuditList(ActivityAuditDTO activityAuditDTO, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(activityMapper.getActivityAuditList(activityAuditDTO));

    }

    @Override
    public Activity getActivityByQuestionnaireId(Integer questionnaireId) {
        return activityMapper.getActivityByQuestionnaireId(questionnaireId);
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

    @Override
    public String packageAndDownloadActivityFiles(Integer activityId) throws IOException {


        // 1. 获取活动信息和文件列表
        Activity activity = activityMapper.findById(activityId);
        List<ActivityFiles> fileDetails = activityFilesMapper.findByActivityId(activityId);

        // 2. 创建临时目录
        Path tempDir = Files.createTempDirectory("activity_download_");

        try {
            // 3. 创建压缩包
            Path zipPath = tempDir.resolve(generateZipFileName(activity));
            createActivityZipFile(activity, fileDetails, zipPath);

            // 4. 上传到OSS
            String ossKey = "downloads/" + zipPath.getFileName().toString();
            uploadToOSS(zipPath, ossKey);

            // 5. 生成签名URL
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
    /*private String saveFileToStorage(MultipartFile file,Integer activityId,Integer sort,String storageName) throws IOException {
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
    }*/
    /**
     * OSS保存文件到存储目录
     */
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

    private String generateZipFileName(Activity activity) {
        return String.format("activity_%s_%s.zip",
                activity.getTitle(),
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
                    getFileCategory(file.getFileSort()),
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

    /**
     * 根据文件排序类型获取分类名称
     */
    private String getFileCategory(Integer fileSort) {
        return switch (fileSort) {
            case 1, 2, 3 -> "签到";
            case 4 -> "活动过程";
            case 5 -> "纸质新闻稿";
            case 6 -> "附件";
            default -> throw new IllegalArgumentException("无效的文件排序类型: " + fileSort);
        };
    }

}
