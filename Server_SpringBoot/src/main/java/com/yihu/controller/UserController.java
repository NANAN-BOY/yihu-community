package com.yihu.controller;

import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import com.yihu.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/register")
    public ApiResponse<String> register(@RequestParam String phone, @RequestParam String userName, @RequestParam String password, @RequestParam String captcha){
        int isSuccess = userService.register(phone, userName, password, captcha);//注册用户
        if(isSuccess == 0){
            return ApiResponse.success("注册成功");
        }else if (isSuccess == 1){
            return ApiResponse.error(400,"注册失败");
        }else{
            return ApiResponse.error(500,"验证码错误");
        }
    }
}
