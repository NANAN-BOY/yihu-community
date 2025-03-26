package com.yihu.controller;


import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.entity.InviteExpertRecord;
import com.yihu.entity.Order;
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
    public Result getHistoryRecord(@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize){
        User currentUser = TokenUtils.getCurrentUser();

        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }
        PageInfo<InviteExpertRecord> inviteExpertRecords = expertService.getHistoryRecord(pageNum,pageSize);
        if (inviteExpertRecords.getList().isEmpty()){
            return Result.error(404,"没有更多数据");
        }
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
        if (currentUser.getRole() != 3) {
            return Result.error(403, "您的账号无法接受邀请");
        }
        int isSuccess = expertService.accept(id,isAgree,expertId);
        if (isSuccess == 0){
            return Result.success("成功接受邀请");
        }else {
            return Result.error(500,"接受邀请失败");
        }
    }

    @GetMapping("/expert/get-createRecord")
    public Result getCreateRecord( @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize){
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }

        PageInfo<InviteExpertRecord> pageInfo = expertService.getCreateRecord(pageNum,pageSize);
        if (pageInfo.getList().isEmpty()) {
            return Result.error(404, "没有更多数据");
        }
        return Result.success(pageInfo);
    }

    @GetMapping("/expert/get-byTime")
    public Result getByTime(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime){
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "禁止访问：权限不足");
        }

        PageInfo<InviteExpertRecord> pageInfo = expertService.getByTime(pageNum,pageSize,startTime,endTime);
        if (pageInfo.getList().isEmpty()) {
            return Result.error(404, "没有更多数据");
        }
        return Result.success(pageInfo);
    }

    @GetMapping("/expert/orderList")
    public Result orderList(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 4) {
            return Result.error(403, "禁止访问：权限不足");
        }

        PageInfo<Order> pageInfo = expertService.getOrderList(pageNum, pageSize);
        if (pageInfo.getList().isEmpty()) {
            return Result.error(404, "没有更多数据");
        }
        return Result.success(pageInfo);
    }

    @PostMapping("/expert/preemptOrder")
    public Result preemptOrder(@RequestParam String orderNo, @RequestParam int buyerId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权：请先登录");
        }
        if (currentUser.getRole() != 4) {
            return Result.error(403, "禁止访问：权限不足");
        }
        int isSuccess = expertService.preemptOrder(orderNo, buyerId, currentUser.getId());
        if (isSuccess == 0) {
            return Result.success("成功接受订单");
        } else if (isSuccess == -2) {
            return Result.error(500, "接受订单失败");
        } else {
            return Result.error(500, "该订单已被接受");
        }
    }
}
