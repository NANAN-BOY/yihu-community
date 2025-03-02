package com.yihu.service.impl;

import com.yihu.entiy.User;
import com.yihu.mapper.UserMapper;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    private final CaptchaService captchaService;
    @Autowired
    public UserServiceImpl(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }


    @Override
    public User getUserByPhone(int phopne) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return userMapper.selectAll();
    }

    @Override
    public int register(String userName, String password, String phoneNumber, String capcha) {
        Boolean isVerify = captchaService.verifyCaptcha(phoneNumber,capcha);
        if (isVerify){
            int isSuccess = userMapper.register(userName,password,phoneNumber);
            if (isSuccess > 0){
                return 0;//注册成功
            }else {
                return 1;//注册失败
            }
        }else {
            return -1;//验证码错误
        }
    }
}
