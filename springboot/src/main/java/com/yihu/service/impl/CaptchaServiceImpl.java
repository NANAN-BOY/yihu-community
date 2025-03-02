package com.yihu.service.impl;

import com.yihu.service.CaptchaService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisTemplate<String,String> redisTemplate;

    public CaptchaServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captcha.append(random.nextInt(10));
        }
        return captcha.toString();
    }

    @Override
    public void saveCaptcha(String phone, String captcha) {
        String key = "captcha:" + phone;
        redisTemplate.opsForValue().set(key, captcha, 5, TimeUnit.MINUTES);//将验证码存入redis，有效期为5分钟
    }

    @Override
    public Boolean verifyCaptcha(String phone, String captcha) {
        String key = "captcha:" + phone;
        String storedCaptcha = redisTemplate.opsForValue().get(key);
        return captcha.equals(storedCaptcha);//验证验证码是否正确
    }
}
