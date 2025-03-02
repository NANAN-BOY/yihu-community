package com.yihu.controller;

import com.yihu.common.Result;
import com.yihu.entiy.User;
import com.yihu.exception.CustomException;
import com.yihu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public Result test(){
        List<User> userList =  userService.getAll();
        return Result.success(userList);
    }

}
