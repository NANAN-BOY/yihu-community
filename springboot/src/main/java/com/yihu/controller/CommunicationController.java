package com.yihu.controller;

import com.yihu.common.Result;
import com.yihu.entity.Communication;
import com.yihu.service.CommunicationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    @Resource
    private CommunicationService communicationService;

    /**
     * 查询所有消息
     */
    @GetMapping
    public Result findByFromUsername(@RequestParam String fromUser, @RequestParam String toUser) {
        List<Communication> all = communicationService.findByUsername(fromUser, toUser);
        return Result.success(all);
    }
}
