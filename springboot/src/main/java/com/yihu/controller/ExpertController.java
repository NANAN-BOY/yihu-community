package com.yihu.controller;


import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.entity.InviteExpertRecord;
import com.yihu.entity.User;
import com.yihu.service.ExpertService;
import com.yihu.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpertController {

    private final ExpertService expertService;

    @Autowired
    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    @PostMapping("/expert/create-record")
    public Result createExpert(@RequestParam int inviteUserId,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createAt,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date deadLine){

        User currentUser = TokenUtils.getCurrentUser();

        //  验证用户是否存在
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }

        //  验证用户角色是否为管理员
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }
        return Result.success(expertService.createInviteRecord(inviteUserId,createAt,deadLine));
    }
    @AuthAccess
    @GetMapping("/expert/get-record")
    public Result getRecord(@RequestParam int id){
        return Result.success(expertService.getRecord(id));
    }

    @GetMapping("/expert/get-historyRecord")
    public Result getHistoryRecord(){
        User currentUser = TokenUtils.getCurrentUser();

        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }
        List<InviteExpertRecord> inviteExpertRecords = expertService.getHistoryRecord();
        return Result.success(inviteExpertRecords);
    }

    @PostMapping("/expert/delete-record")
    public Result deleteRecord(@RequestParam int id){
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }
        int isSuccess = expertService.deleteRecord(id);
        if (isSuccess == 0){
            return Result.error(200,"成功取消邀请");
        }else if (isSuccess == -2){
            return Result.error(500,"取消邀请失败");
        }else {
            return Result.error(500,"不能取消已经被接收或拒绝的邀请记录");
        }
    }

    @AuthAccess
    @PostMapping("/expert/refuse")
    public Result refuse(@RequestParam int id, @RequestParam int isAgree,@RequestParam String reason){
        int isSuccess = expertService.refuse(id,isAgree,reason);
        if (isSuccess == 0){
            return Result.success("成功拒绝邀请");
        }else {
            return Result.error(500,"拒绝邀请失败");
        }
    }

    @PostMapping("/expert/accept")
    public Result accept(@RequestParam int id, @RequestParam int isAgree, @RequestParam int expertId){
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() == 4) {
            return Result.error(403, "您已经是专家，无需接受邀请");
        }
        int isSuccess = expertService.accept(id,isAgree,expertId);
        if (isSuccess == 0){
            return Result.success("成功接受邀请");
        }else {
            return Result.error(500,"接受邀请失败");
        }
    }
}
