package com.yihu.server_springboot.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CaptchaService {
    public static String getCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captcha.append(random.nextInt(10));
        }
        return captcha.toString();
    }

}
