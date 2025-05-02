package com.yihu.controller;

import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.dto.ActivityQueryDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import com.yihu.entity.User;
import com.yihu.service.ActivityService;
import com.yihu.service.QuestionnaireService;
import com.yihu.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;
    private final QuestionnaireService questionnaireService;

    @Autowired
    public ActivityController(ActivityService activityService, QuestionnaireService questionnaireService) {
        this.activityService = activityService;
        this.questionnaireService = questionnaireService;
    }


    @PostMapping("/addActivity")
    public Result addActivity(@RequestParam Integer projectId){
        User currentUser = TokenUtils.getCurrentUser();
        // 创建问卷
        int questionId = questionnaireService.create();
        int activityId = activityService.addActivity(projectId,questionId,currentUser.getId());
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

    /**
     * 文件下载接口
     */
    @AuthAccess
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Integer id) {
        try {
            // 获取文件信息
            ActivityFiles activityFilesEntity = activityService.getFileById(id);
            if (activityFilesEntity == null) {
                log.error("File not found: {}", id);
                return ResponseEntity.notFound().build();

            }

            // 获取文件路径
            java.io.File file = new java.io.File(activityFilesEntity.getFileUrl());
            if (!file.exists()) {
                log.error("File not found at path: {}", activityFilesEntity.getFileUrl());
                return ResponseEntity.notFound().build();
            }

            // 创建 InputStreamResource
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));


            // 3. 获取文件名和扩展名
            String fileName = activityFilesEntity.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            // 4. 验证文件类型
            boolean isValid = false;
            String mimeType = "application/octet-stream"; // 默认类型
            switch (extension) {
                case "jpg":
                case "jpeg":
                    mimeType = "image/jpeg";
                    isValid = true;
                    break;
                case "png":
                    mimeType = "image/png";
                    isValid = true;
                    break;
                case "zip":
                    mimeType = "application/zip";
                    isValid = true;
                    break;
                case "rar":
                    mimeType = "application/x-rar-compressed";
                    isValid = true;
                    break;
                case "7z":
                    mimeType = "application/x-7z-compressed";
                    isValid = true;
                    break;
                default:
                    // 不支持的文件类型
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new InputStreamResource(new ByteArrayInputStream("不支持的文件类型".getBytes())));
            }

            // 5. 构建响应
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                            + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()) + "\"")
                    .contentType(MediaType.valueOf(mimeType))
                    .body(resource);

        } catch (FileNotFoundException e) {
            log.error("File not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @AuthAccess
    @GetMapping("/getFileInformation/{id}")
    public Result getFileInformation(@PathVariable Integer id) {
        ActivityFiles activityFiles = activityService.getFileById(id);
        if(activityFiles != null){
            return Result.success(activityFiles);
        }
        else {
            return Result.error("文件不存在");
        }
    }


    @DeleteMapping("/deleteFileById")
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
                // 先删除旧数据 物理删除非逻辑删除
                activityService.physicalDeleteNewsByActivityId(activityDTO.getActivityId());
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


    /*@GetMapping("/queryByCreateId") // 查询用户活动列表
    public Result queryByCreateId(@RequestParam(required = false) String title,
                        @RequestParam(defaultValue = "1") int pageNum,
                        @RequestParam(defaultValue = "10") int pageSize) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("用户未登录");
            }
            if (currentUser.getRole() == 1){
                // 管理员查询 查询所有已经提交的 并且项目没有提交的
                PageInfo<Activity> activityList = activityService.queryAllSubmited(title,pageNum,pageSize);
                return Result.success(activityList);
            }else{
                // 用户查询 查询当前用户未删除的
                PageInfo<Activity> activityList = activityService.getActivityByProjectId(currentUser.getId(),title,pageNum,pageSize);
                System.out.println(activityList);
                return Result.success(activityList);
            }

        } catch (Exception e) {
            log.error("查询用户活动列表失败", e);
            return Result.error("查询失败");
        }
    }*/
    @GetMapping("/getActivityAuditList")
    public Result getActivityAuditList(@ModelAttribute ActivityAuditDTO activityAuditDTO,
                                       @RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageInfo<ActivityAuditDTO> activityAuditList = activityService.getActivityAuditList(activityAuditDTO,pageNum,pageSize);
            return Result.success(activityAuditList);
        }
        catch (Exception e) {
            log.error("查询失败", e);
            return Result.error("查询失败");
        }
    }

    @GetMapping("/getActivityByProjectId")
    public Result getActivityByProjectId(@RequestParam Integer projectId,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageInfo<Activity> activityList = activityService.getActivityByProjectId(projectId,title,pageNum,pageSize);
            return Result.success(activityList);
        } catch (Exception e) {
            log.error("查询失败", e);
            return Result.error("查询失败");
        }
    }

    @PostMapping("/submitActivity")
    public Result submitActivity(@RequestBody ActivityDTO activityDTO) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            activityService.updateStatus(activityDTO.getActivityId(),1,currentUser.getId(),null);
            return Result.success("保存并提交成功");
        } catch (Exception e){
            return Result.error("保存并提交失败");
        }
    }

    @PostMapping("/withdrawSubmission")  //管理员撤回提交申请
    public Result withdrawSubmission(@RequestParam Integer activityId,
                                    @RequestParam String reason) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            activityService.updateStatus(activityId,2, currentUser.getId(),reason);
            return Result.success("撤回提交成功");
        } catch (Exception e) {
            log.error("撤回提交失败", e);
            return Result.error("撤回提交失败");
        }
    }

    @PostMapping("/approved")  //管理员撤回提交申请
    public Result approved(@RequestParam Integer activityId) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            activityService.updateStatus(activityId,3,currentUser.getId(),null);
            return Result.success("审核通过成功");
        } catch (Exception e) {
            log.error("审核通过失败", e);
            return Result.error("审核通过失败");
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

    @AuthAccess
    @GetMapping("/getActivityByQuestionnaireId")
    public Result getActivityByQuestionnaireId(@RequestParam Integer questionnaireId) {
        try {
            // 查询活动基本信息
            Activity activity = activityService.getActivityByQuestionnaireId(questionnaireId);
            return Result.success(activity.getTitle());
        } catch (Exception e) {
            log.error("获取活动详情失败", e);
            return Result.error("获取活动详情失败");
        }
    }











}






