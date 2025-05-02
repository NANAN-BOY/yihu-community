package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
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
public class ActivityServiceImpl implements ActivityService {

    //@Value("${file.storage.dir}") // 从配置文件中读取文件存储目录
    private String storageDir = System.getProperty("user.dir") + "/files/";

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityFilesMapper activityFilesMapper;
    @Autowired
    private ActivityNewsMapper activityNewsMapper;


    @Override
    public int uploadFile(MultipartFile file, Integer activityId,Integer sort) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException {

        // 5. 生成唯一文件名（时间戳 + 原始文件名） 防止重名文件
        String storageName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String storagePath = saveFileToStorage(file,activityId,sort,storageName);

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
    public void deleteActivityById(Integer id,Integer userId) {
        // 删除活动记录
        Activity activity = new Activity();
        activity.setId(id);
        activity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setUpdateById(userId);

        activityMapper.deleteById(activity);

        // 数据库全部删除完成，删除活动id文件夹
        Path titleDir = Paths.get(storageDir, id.toString());
        try {
            if (Files.exists(titleDir)) {
                FileUtil.del(titleDir.toFile()); // 使用Hutool工具类删除文件夹
                log.info("成功删除文件夹: {}", titleDir);
            }
        } catch (Exception e) {
            log.error("删除文件夹失败: {}", titleDir, e);
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
            Files.deleteIfExists(Paths.get(filePath)); // 删除本地文件
            log.info("成功删除文件: {}", filePath);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
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
