package com.yihu.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {
    private final StringRedisTemplate redisTemplate;
    public static String getCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captcha.append(random.nextInt(10));
        }
        return captcha.toString();
    }//生成6位随机验证码
    public CaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void saveCaptcha(String phone, String captcha) {
        String key = "captcha:" + phone;
        redisTemplate.opsForValue().set(key, captcha, 5, TimeUnit.MINUTES);//将验证码存入redis，有效期为5分钟
    }
    public Boolean verifyCaptcha(String phone, String captcha) {
        String key = "captcha:" + phone;
        String storedCaptcha = redisTemplate.opsForValue().get(key);
        return captcha.equals(storedCaptcha);//验证验证码是否正确
    }
}
