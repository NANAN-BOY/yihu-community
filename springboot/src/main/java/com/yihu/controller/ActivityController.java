package com.yihu.controller;

import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.User;
import com.yihu.service.ActivityService;
import com.yihu.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/activity")
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
    /*
    *

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
    *
    * */



    @PostMapping("/addActivity")
    public Result addActivity(){
        User currentUser = TokenUtils.getCurrentUser();
        int activityId = activityService.addActivity(currentUser.getId());
        if(activityId != 0){
            return Result.success(activityId);
        }
        return Result.error();
    }


    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam Integer sort, @RequestParam Integer activityId){

        try{
            int newId = activityService.uploadFile(file,activityId,sort);
            if (newId != 0){
                // 返回文件的所有信息
                ActivityFiles activityFiles = activityService.getById(newId);
                return Result.success(activityFiles);
            }else {
                return Result.error("文件上传失败");
            }
        } catch (Exception e){
            return Result.error("文件上传失败");
        }

    }

    @DeleteMapping("deleteFileById")
    public Result deleteFileById(@RequestParam Integer fileId){
        try {
            activityService.deleteFile(fileId);
            return Result.success();
        }catch (Exception e){
            return Result.error("文件删除失败");
        }
    }

    @PostMapping("/update")
    public Result update(@RequestBody ActivityDTO activityDTO) {
        User currentUser = TokenUtils.getCurrentUser();
        try {
            // 更新activity表
            activityService.update(activityDTO,currentUser.getId());;
            // 获取 news 数据并更新
            List<ActivityDTO.NewsItem> newsList = activityDTO.getNews();
            if (newsList != null) {
                // 先删除旧数据
                activityService.deleteNewsByActivityId(activityDTO.getActivityId());
                // 遍历newsList 插入新数据
                if (newsList != null) {
                    // 遍历newsList 插入新数据
                    for (ActivityDTO.NewsItem newsItem : newsList) {
                        String platform = newsItem.getPlatform();
                        String link = newsItem.getLink();
                        // 插入到数据库...
                        activityService.insertnews(activityDTO.getActivityId(), platform,link);
                    }
                }
            }
            return Result.success("更新成功");
        }catch (Exception e){
            return Result.error("更新失败");
        }
    }



    @AuthAccess
    @DeleteMapping("/deleteById")
    public Result delete(@PathVariable ActivityDTO activityDTO) {

        // 解析 JSON
        //ObjectMapper objectMapper = new ObjectMapper();
        //ActivityCreateDTO activityDTO = objectMapper.readValue(activityDataJson, ActivityCreateDTO.class);

        // 删除活动记录
        activityService.deleteActivityById(activityDTO.getActivityId(), activityDTO.getTitle());

        // 删除与该活动相关的新闻记录
        activityService.deleteNewsByActivityId(activityDTO.getActivityId());

        // 删除与该活动相关的文件记录
        activityService.deleteFilesByActivityId(activityDTO.getActivityId());

        return Result.success();
    }


    @GetMapping("/queryByCreateId") // 查询用户活动列表
    public Result query(@RequestBody ActivityDTO activityDTO,
                        @RequestParam(defaultValue = "1") int pageNum,
                        @RequestParam(defaultValue = "10") int pageSize) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("用户未登录");
            }
            PageInfo<Activity> activityList = activityService.queryByCreateId(currentUser.getId(),activityDTO,pageNum,pageSize);
            System.out.println(activityList);
            return Result.success(activityList);
        } catch (Exception e) {
            log.error("查询用户活动列表失败", e);
            return Result.error("查询失败");
        }
    }

    @PostMapping("/submitActivity")
    public Result submitActivity(@RequestBody ActivityDTO activityDTO) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            activityService.submitActivity(activityDTO,currentUser.getId());
            return Result.success("保存并提交成功");
        } catch (Exception e){
            return Result.error("保存并提交失败");
        }

    }







}






