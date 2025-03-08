package com.yihu.controller;

import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.entity.User;
import com.yihu.exception.ServiceException;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import com.yihu.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/user/register")
    public Result register(@RequestParam String userName, @RequestParam String password,
                           @RequestParam String phoneNumber, @RequestParam String captcha,
                           @RequestParam String location){
        int isSuccess = userService.register(userName,password,phoneNumber,captcha,location);
        if (isSuccess == 0){
            return Result.success("注册成功");
        }else if (isSuccess == 1){
            return Result.error(404,"注册失败");
        }else if (isSuccess == -2){
            return Result.error(404,"手机号已注册");
        }
        else {
            return Result.error(500, "验证码错误");
        }
    }

    @AuthAccess
    @PostMapping("/user/login")
    public Result login(@RequestParam String phone, @RequestParam String password){
        User user = userService.login(phone,password);
        if (user == null){
            throw new ServiceException("用户名或密码错误");
        }
        //生成token
        String token = TokenUtils.getToken(user.getPhone(), user.getPassword(), user.getRole());
        return Result.success(user,token);
    }

    @GetMapping("/user/info")
    public Result getUserInfo(){
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null){
            return Result.error(401,"用户未登录");
        }
        return Result.success(currentUser);
    }

    @AuthAccess
    @PostMapping("/user/reset-password")
    public Result resetPassword(@RequestParam String phone, @RequestParam String captcha, @RequestParam String newPassword){
        int isSuccess = userService.resetPassword(phone,captcha,newPassword);
        if (isSuccess == 0){
            return Result.success("修改成功");
        }else if (isSuccess == -1){
            return Result.error(404,"验证码错误");
        }else {
            return Result.error(500,"修改失败");
        }
    }
}
