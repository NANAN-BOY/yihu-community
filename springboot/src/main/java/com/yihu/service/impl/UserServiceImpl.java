package com.yihu.service.impl;

import com.yihu.entity.User;
import com.yihu.mapper.UserMapper;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
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
    public int register(String userName, String password, String phoneNumber, String captcha, String location) {
        Boolean isVerify = captchaService.verifyCaptcha(phoneNumber,captcha);
        if (isVerify){
            Date currentDate = new Date(); // 获取当前日期时间
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime()); // 将日期时间转换为Timestamp类型
            User user = new User(userName,phoneNumber,password,location,3,0,0,currentTimestamp,currentTimestamp,1,currentTimestamp,0);
            int isSuccess = userMapper.register(user);
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
