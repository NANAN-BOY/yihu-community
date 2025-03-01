package com.yihu.service;

import com.yihu.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CaptchaService captchaService;
    private final UserDao userDao;
    @Autowired
    public UserService(CaptchaService captchaService, UserDao userDao) {
        this.captchaService = captchaService;
        this.userDao = userDao;
    }

    public int register(String userName, String password, String phoneNumber, String captcha) {
        Boolean isValid = captchaService.verifyCaptcha(phoneNumber, captcha); // 验证验证码
        if (isValid) {
            Boolean isSuccessful =userDao.resigister(userName, password, phoneNumber); // 注册用户
            if (isSuccessful) {
                return 0; // 注册成功
            }else {
                return 1; // 注册失败
            }
        }else {
            return -1; // 验证码错误
        }
    }
}
