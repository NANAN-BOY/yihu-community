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
    public int uploadFile(MultipartFile file, Integer activityId,Integer sort,String title) throws IOException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException {

        // 文件不存在，保存到存储目录
        String storagePath = saveFileToStorage(file,title,sort);

        // 创建新的文件实体
        ActivityFiles activityFiles = new ActivityFiles();
        activityFiles.setActivityId(activityId);
        activityFiles.setName(file.getOriginalFilename());
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


    /**
     * 保存文件到存储目录
     */
    private String saveFileToStorage(MultipartFile file,String title,Integer sort) throws IOException {
        // 2. 获取文件后缀（如 ".jpg"）
        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));

        // 3. 根据sort决定子目录名称
        String subDir = switch(sort) {
            case 2 -> "签到";
            case 3 -> "活动过程";
            case 4 -> "纸质新闻稿";
            case 5 -> "附件";
            default -> throw new IllegalArgumentException("非法的sort值: " + sort);
        };

        // 4. 构建完整存储路径（格式：根目录/活动标题/子目录/）
        // 例如：/storage/春季郊游/签到/
        Path targetDir = Paths.get(storageDir, title, subDir);
        Files.createDirectories(targetDir); // 自动创建多级目录

        // 5. 生成最终文件名（保留原始文件名）
        String fileName = file.getOriginalFilename();
        Path targetPath = targetDir.resolve(fileName);


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
        // 保存文件
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return targetPath.toString();
    }
}
