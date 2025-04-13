package com.yihu.controller;

import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.ActivityDTO;
import com.yihu.dto.ActivityQueryDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
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
    @DeleteMapping("/deleteById") // 表数据不删除，存储的文件需要删除
    public Result delete(@RequestParam Integer activityId) {

        User currentUser = TokenUtils.getCurrentUser();

        // 删除与该活动相关的新闻记录
        activityService.deleteNewsByActivityId(activityId);

        // 删除与该活动相关的文件记录
        activityService.deleteFilesByActivityId(activityId);


        // 删除活动记录
        activityService.deleteActivityById(activityId, currentUser.getId());

        return Result.success("删除活动成功");
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

    @GetMapping("/getActivityById")
    public Result getActivityById(@RequestParam Integer activityId) {
        try {
            // 查询活动基本信息
            Activity activity = activityService.getActivityById(activityId);

            // 查询活动相关文件列表
            List<ActivityFiles> files = activityService.getFilesByActivityId(activityId);

            // 查询活动相关新闻列表
            List<ActivityNews> news = activityService.getNewsByActivityId(activityId);

            // 封装到 ActivityDetailDTO
            ActivityQueryDTO activityQueryDTO = new ActivityQueryDTO();
            activityQueryDTO.setActivity(activity);
            activityQueryDTO.setFiles(files);
            activityQueryDTO.setNews(news);

            return Result.success(activityQueryDTO);
        } catch (Exception e) {
            log.error("获取活动详情失败", e);
            return Result.error("获取活动详情失败");
        }
    }








}






