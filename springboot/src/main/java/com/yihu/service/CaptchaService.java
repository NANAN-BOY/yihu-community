package com.yihu.service;

public interface CaptchaService {

    String getCaptcha();//生成验证码

    void saveCaptcha(String phone, String captcha);//存储验证码

    Boolean verifyCaptcha(String phone, String captcha);//校验验证码
}
