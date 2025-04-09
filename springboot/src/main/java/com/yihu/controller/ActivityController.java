package com.yihu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.ActivityCreateDTO;
import com.yihu.entity.File;
import com.yihu.service.ActivityService;
import com.yihu.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 文件上传接口
     *
     * // 1. 构造 JSON 数据
     * const activityData = {
     *     status: 1,
     *     title: "志愿活动",
     *     noticeContent: "活动详情...",
     *     staffCount: 10,
     *     volunteerCount: 50,
     *     serviceObjectCount: 100,
     *     news: [  // 包含多个平台链接
     *         { platform: "知乎", url: "https://www.zhihu.com" },
     *         { platform: "微信", url: "https://weixin.qq.com" }
     *     ]
     * };
     *
     * // 2. 构造 FormData（如果有文件上传）
     * const formData = new FormData();
     * formData.append("activityData", JSON.stringify(activityData)); // JSON 数据
     * // formData.append("files", file1); // 如果有文件，可以这样添加
     *
     * // 3. 发送请求
     * axios.post("/api/activity/create", formData, {
     *     headers: { "Content-Type": "multipart/form-data" }
     * });
     */
    @AuthAccess
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result create(@RequestPart("signfiles") MultipartFile[] signfiles,
                         @RequestPart("activityfiles") MultipartFile[] activityfiles,
                         @RequestPart("newsfiles") MultipartFile[] newsfiles,
                         @RequestPart("attachmentfile") MultipartFile attachmentfile,
                         @RequestPart("activityData") String activityDataJson) throws JsonProcessingException {


        // 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        ActivityCreateDTO activityDTO = objectMapper.readValue(activityDataJson, ActivityCreateDTO.class);

        // 1. 删除旧数据（在创建新活动之前）
        // 假设 activityDTO 中包含活动的唯一标识（如 activityId）
        if (activityDTO.getId() != null) {
            // 更新activity记录
            activityService.deleteFilesByActivityId(activityDTO.getId());

            // 删除与该活动相关的新闻记录
            activityService.deleteNewsByActivityId(activityDTO.getId());

            // 删除活动记录（如果需要）
            activityService.deleteActivityById(activityDTO.getId(),activityDTO.getTitle());
        }

        // 2. 创建新活动

        try {
            int activityId = activityService.create(activityDTO);;
            if (activityId != 0){
                // 遍历签到照片
                for (MultipartFile file : signfiles) {
                    if (!file.isEmpty()) {
                        // 保存文件到本地或云存储...
                        activityService.uploadFile(file,activityId,2,activityDTO.getTitle());
                    }
                }
                // 遍历活动过程照片
                for (MultipartFile file : activityfiles) {
                    if (!file.isEmpty()) {
                        // 保存文件到本地或云存储...
                        activityService.uploadFile(file,activityId,3,activityDTO.getTitle());
                    }
                }
                // 遍历纸质新闻稿
                if (newsfiles != null) {
                    for (MultipartFile file : newsfiles) {
                        if (!file.isEmpty()) {
                            // 保存文件到本地或云存储...
                            activityService.uploadFile(file,activityId,4,activityDTO.getTitle());
                        }
                    }
                }

                // 判断附件是否有附件
                if (!attachmentfile.isEmpty()) {
                    String fileName = attachmentfile.getOriginalFilename();
                    System.out.println("接收到文件: " + fileName);
                    // 保存文件到本地或云存储...
                    activityService.uploadFile(attachmentfile,activityId,6,activityDTO.getTitle());
                }

                // 获取 news 数据并遍历
                List<ActivityCreateDTO.NewsItem> newsList = activityDTO.getNews();
                if (newsList != null) {
                    for (ActivityCreateDTO.NewsItem newsItem : newsList) {
                        String platform = newsItem.getPlatform();
                        String link = newsItem.getLink();
                        System.out.println("平台: " + platform + ", URL: " + link);
                        // 存储到数据库...
                        activityService.insertnews(activityId, platform,link);
                    }
                }


                return Result.success();
            }else {
                return Result.error("保存失败");
            }
        } catch (Exception e) {
            return Result.error("保存失败");
        }

    }


    @AuthAccess
    @DeleteMapping("/deleteById")
    public Result delete(@PathVariable ActivityCreateDTO activityCreateDTO) {
        // 删除活动记录
        activityService.deleteActivityById(activityCreateDTO.getId(),activityCreateDTO.getTitle());

        // 删除与该活动相关的新闻记录
        activityService.deleteNewsByActivityId(activityCreateDTO.getId());

        // 删除与该活动相关的文件记录
        activityService.deleteFilesByActivityId(activityCreateDTO.getId());

        return Result.success();
    }
}






