package com.yihu.controller;

import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.ActivityDTO;
import com.yihu.dto.ActivityQueryDTO;
import com.yihu.entity.*;
import com.yihu.service.ActivityService;
import com.yihu.service.ProjectService;
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
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ActivityService activityService;

    @Autowired
    public ProjectController(ProjectService projectService, ActivityService activityService) {
        this.projectService = projectService;
        this.activityService = activityService;
    }



    @PostMapping("/addProject")
    public Result addActivity(@RequestParam String name){
        User currentUser = TokenUtils.getCurrentUser();
        int projectId = projectService.addProject(currentUser.getId(),name);
        if(projectId != 0){
            return Result.success(projectId);
        }
        return Result.error();
    }


    @PostMapping("/update")
    public Result update(@RequestBody Project project) {
        User currentUser = TokenUtils.getCurrentUser();
        try {
            // 更新activity表
            projectService.update(project,currentUser.getId());;
            return Result.success("更新成功");
        }catch (Exception e){
            return Result.error("更新失败");
        }
    }



    @AuthAccess
    @DeleteMapping("/deleteById") // 表数据不删除，存储的文件需要删除
    public Result delete(@RequestParam Integer projectId) {
        User currentUser = TokenUtils.getCurrentUser();
        try {
            // 获取该项目下的所有活动ID
            List<Activity> activities = activityService.getActivityIdByProjectId(projectId);
            if (activities == null || activities.isEmpty()) {
                projectService.deleteProjectById(projectId, currentUser.getId());
                return Result.success("删除项目成功");
            }

            for (Activity activity : activities) {
                Integer activityId = activity.getId();
                // 删除与该活动相关的新闻记录
                activityService.deleteNewsByActivityId(activityId);
                // 删除与该活动相关的文件记录
                activityService.deleteFilesByActivityId(activityId);
                // 删除活动记录
                activityService.deleteActivityById(activityId, currentUser.getId());
            }

            // 删除项目记录
            projectService.deleteProjectById(projectId, currentUser.getId());

            return Result.success("删除项目成功");
        } catch (Exception e) {
            log.error("删除项目失败，projectId: " + projectId, e);
            return Result.error("删除项目失败：" + e.getMessage());
        }
    }



    @GetMapping("/queryByCreateId") // 查询用户活动列表
    public Result queryByCreateId(@ModelAttribute Project project,
                        @RequestParam(defaultValue = "1") int pageNum,
                        @RequestParam(defaultValue = "10") int pageSize) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("用户未登录");
            }
            if (currentUser.getRole() == 1){
                // 管理员查询 查询所有已经提交的
                PageInfo<Project> activityList = projectService.queryAllSubmited(project,pageNum,pageSize);
                return Result.success(activityList);
            }else{
                // 用户查询 查询当前用户未删除的
                PageInfo<Project> activityList = projectService.queryByCreateId(currentUser.getId(),project,pageNum,pageSize);
                System.out.println(activityList);
                return Result.success(activityList);
            }

        } catch (Exception e) {
            log.error("查询用户活动列表失败", e);
            return Result.error("查询失败");
        }
    }

    @PostMapping("/submitProject")
    public Result submitProject(Project project) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            projectService.updateStatus(project.getId(),1,currentUser.getId());
            return Result.success("提交成功");
        } catch (Exception e){
            return Result.error("提交失败");
        }

    }

    @PostMapping("/withdrawSubmission")  //管理员撤回提交申请
    public Result withdrawSubmission(@RequestParam Integer projectId) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            projectService.updateStatus(projectId,2, currentUser.getId());
            return Result.success("撤回提交成功");
        } catch (Exception e) {
            log.error("撤回提交失败", e);
            return Result.error("撤回提交失败");
        }
    }

    @PostMapping("/approved")  //管理员撤回提交申请
    public Result approved(@RequestParam Integer projectId) {
        try {
            User currentUser = TokenUtils.getCurrentUser();
            projectService.updateStatus(projectId,3,currentUser.getId());
            return Result.success("审核通过成功");
        } catch (Exception e) {
            log.error("审核通过失败", e);
            return Result.error("审核通过失败");
        }
    }


    @GetMapping("/getProjectById")
    public Result getProjectById(@RequestParam Integer projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            return Result.success(project);
        } catch (Exception e) {
            log.error("获取项目详情失败", e);
            return Result.error("获取项目详情失败");
        }
    }

}






