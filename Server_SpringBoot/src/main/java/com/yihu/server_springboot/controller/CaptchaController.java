package com.yihu.server_springboot.controller;

import com.yihu.server_springboot.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;

    @Autowired
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    // 生成验证码
    @GetMapping("/generate")
    public String generateCaptcha(@RequestParam String phone) {
        // 生成验证码
        String captcha = CaptchaService.getCaptcha();
        System.out.println(captcha);
        // 将验证码保存到 Redis
        captchaService.saveCaptcha(phone, captcha);
        // 这里可以添加发送验证码的逻辑（比如短信发送）
        // 例如：sendSms(phone, captcha);

        return "验证码已发送";
    }

    // 验证验证码
    @PostMapping("/verify")
    public String verifyCaptcha(@RequestParam String phone, @RequestParam String captcha) {
        boolean isValid = captchaService.verifyCaptcha(phone, captcha);
        System.out.println(phone);
        System.out.println(captcha);
        if (isValid) {
            return "验证码验证通过";
        } else {
            return "验证码错误";
        }
    }
}
