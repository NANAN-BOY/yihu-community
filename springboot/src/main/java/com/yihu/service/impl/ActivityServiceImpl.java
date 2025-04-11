package com.yihu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.yihu.dto.ActivityCreateDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import com.yihu.entity.File;
import com.yihu.mapper.ActivityFilesMapper;
import com.yihu.mapper.ActivityMapper;
import com.yihu.mapper.ActivityNewsMapper;
import com.yihu.mapper.FileMapper;
import com.yihu.service.ActivityService;
import com.yihu.service.FileService;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HexFormat;

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
    public int create(ActivityCreateDTO activityCreateDTO) {
        Activity activity = new Activity();
        activity.setTitle(activityCreateDTO.getTitle());
        activity.setNoticeContent(activityCreateDTO.getNoticeContent());
        activity.setStaffCount(activityCreateDTO.getStaffCount());
        activity.setVolunteerCount(activityCreateDTO.getVolunteerCount());
        activity.setServiceObjectCount(activityCreateDTO.getServiceObjectCount());
        activity.setStatus(activityCreateDTO.getStatus());
        activity.setDelFlag("N");
        activity.setlastUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setlastUpdateById(1);

        activityMapper.create(activity);

        //返回新增数据的id
        return activity.getId();
    }

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

        //返回新增数据的id
        return activityFilesMapper.insert(activityFiles);
    }

    @Override
    public int insertnews(Integer activityId, String platform, String link) {

        ActivityNews activityNews = new ActivityNews();
        activityNews.setActivityId(activityId);
        activityNews.setNewsPlatform(platform);
        activityNews.setNewsLink(link);
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
    public void deleteActivityById(Integer id,String title) {
        // 删除活动记录
        activityMapper.deleteById(id);

        // 数据库全部删除完成，删除title文件夹
        Path titleDir = Paths.get(storageDir, title);
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
    public int addActivity() {
        Activity activity = new Activity();
        activity.setDelFlag("N");
        activity.setlastUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setlastUpdateById(1);

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
    public void update(ActivityCreateDTO activityDTO) {
        Activity activity = new Activity();
        activity.setId(activityDTO.getActivityId());
        activity.setTitle(activityDTO.getTitle());
        activity.setNoticeContent(activityDTO.getNoticeContent());
        activity.setStaffCount(activityDTO.getStaffCount());
        activity.setVolunteerCount(activityDTO.getVolunteerCount());
        activity.setServiceObjectCount(activityDTO.getServiceObjectCount());
        activity.setStatus(activityDTO.getStatus());
        activity.setDelFlag("N");
        activity.setlastUpdateTime(new Timestamp(System.currentTimeMillis()));
        activity.setlastUpdateById(1);

        activityMapper.update(activity);

    }



    /**
     * 保存文件到存储目录
     */
    private String saveFileToStorage(MultipartFile file,Integer activityId,Integer sort,String storageName) throws IOException {
        // 1. 获取文件后缀（如 ".jpg"）
        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));

        // 2. 根据sort决定子目录名称
        String subDir = switch(sort) {
            case 2 -> "签到";
            case 3 -> "活动过程";
            case 4 -> "纸质新闻稿";
            case 5 -> "附件";
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
