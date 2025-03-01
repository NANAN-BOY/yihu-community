package com.yihu.controller;

import com.yihu.service.CaptchaService;
import com.yihu.utils.ApiResponse;
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
    public ApiResponse<String> generateCaptcha(@RequestParam String phone) {
        // 生成验证码
        String captcha = CaptchaService.getCaptcha();
        // 将验证码保存到 Redis
        captchaService.saveCaptcha(phone, captcha);
        System.out.println(captcha);
        // 这里可以添加发送验证码的逻辑
        return ApiResponse.success("验证码已发送");
    }

    // 验证验证码
    @PostMapping("/verify")
    public ApiResponse<String> verifyCaptcha(@RequestParam String phone, @RequestParam String captcha) {
        boolean isValid = captchaService.verifyCaptcha(phone, captcha);
        if (isValid) {
            return ApiResponse.success("验证码验证通过");
        } else {
            return ApiResponse.error(400, "验证码错误");
        }
    }
}
