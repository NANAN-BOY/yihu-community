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

}
