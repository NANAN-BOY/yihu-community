package com.yihu.controller;

import com.yihu.common.Result;
import com.yihu.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;
    @Autowired
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/generate")
    public Result generateCaptcha(String phone) {
        System.out.println("1");
        //生成验证码
        String captcha = captchaService.getCaptcha();
        System.out.println(captcha);
        //将验证码存入数据库（5分钟有效）
        captchaService.saveCaptcha(phone, captcha);
        //此处需要发送短信的逻辑（尚未完成）
        Boolean isSuccess = true;//此处用来代表发送短信是否成功
        if (isSuccess) {
            return Result.success("验证码发送成功");
        }else {
            return Result.error(500,"验证码发送失败");
        }
    }
    @PostMapping("/verify")
    public Result verifyCaptcha(String phone, String captcha) {
        //校验验证码与redis中存储的验证码是否一致测试
        Boolean isRight = captchaService.verifyCaptcha(phone, captcha);
        if (isRight) {
            return Result.success("验证码校验成功");
        }else {
            return Result.error(500,"验证码校验失败");
        }
    }
}
