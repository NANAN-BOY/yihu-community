package com.yihu.controller;

import com.yihu.common.Result;
import com.yihu.entiy.User;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseController {

    private final UserService userService;

    @Autowired
    public BaseController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
    }

    @GetMapping ("/login")
    public Result test(){
        List<User> userList =  userService.getAll();
        System.out.println(userList);
        return Result.success(userList);
    }

    @GetMapping("/register")
    public Result register(@RequestParam String userName, @RequestParam String password, @RequestParam String phoneNumber, @RequestParam String captcha){
        int isSuccess = userService.register(userName,password,phoneNumber,captcha);
        if (isSuccess == 0){
            return Result.success("注册成功");
        }else if (isSuccess == 1){
            return Result.error(404,"注册失败");
        }else {
            return Result.error(500, "验证码错误");
        }
    }

}
