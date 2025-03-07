package com.yihu.controller;

import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.entity.User;
import com.yihu.exception.ServiceException;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import com.yihu.utils.TokenUtils;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseController {

    private final UserService userService;

    @Autowired
    public BaseController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping ("/login")
    public Result test(){
        List<User> userList =  userService.getAll();
        System.out.println(userList);
        return Result.success(userList);
    }

    @AuthAccess
    @PostMapping("/register")
    public Result register(@RequestParam String userName, @RequestParam String password,
                           @RequestParam String phoneNumber, @RequestParam String captcha,
                           @RequestParam String location){
        int isSuccess = userService.register(userName,password,phoneNumber,captcha,location);
        if (isSuccess == 0){
            return Result.success("注册成功");
        }else if (isSuccess == 1){
            return Result.error(404,"注册失败");
        }else {
            return Result.error(500, "验证码错误");
        }
    }

    @AuthAccess
    @PostMapping("/login")
    public Result login(@RequestParam String phone, @RequestParam String password){
        User user = userService.login(phone,password);
        if (user == null){
            throw new ServiceException("用户名或密码错误");
        }
        //生成token
        String token = TokenUtils.getToken(user.getPhone(), user.getPassword());
        return Result.success(user,token);
    }
}
