package com.yihu.controller;

import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/expert")
public class ExpertController {

    private final ExpertService expertService;

    @Autowired
    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    @AuthAccess
    @PostMapping("/create")
    public Result createExpert(@RequestParam int inviteUserId,@RequestParam Date createAt,@RequestParam Date deadLine){
        return Result.success(expertService.createInviteRecord(inviteUserId,createAt,deadLine));
    }
}
