package com.yihu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yihu.dto.UserQueryDTO;
import com.yihu.dto.UserUpdateDTO;
import com.yihu.entity.User;
import com.yihu.mapper.UserMapper;
import com.yihu.service.CaptchaService;
import com.yihu.service.UserService;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public User getUserByPhone(String phone) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return userMapper.selectAll();
    }

    @Override
    public int register(String userName, String password, String phoneNumber, String captcha, String location) {
        if (userMapper.selectByPhone(phoneNumber)){
            return -2;//手机号已注册
        }
        Boolean isVerify = captchaService.verifyCaptcha(phoneNumber,captcha);
        if (isVerify){
            Date currentDate = new Date(); // 获取当前日期时间
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime()); // 将日期时间转换为Timestamp类型
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(userName,phoneNumber,hashedPassword,location,3,0,0,currentTimestamp,currentTimestamp,1,currentTimestamp,0);
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

    @Override
    public User login(String phone, String password) {
        User user = null;
        try {
            user = userMapper.login(phone);
        } catch (Exception e) {
            System.out.println("数据库查询异常：" + e.getMessage());
            e.printStackTrace();
        }

        if (user != null) {
            String hashedPassword = user.getPassword();
            if (BCrypt.checkpw(password, hashedPassword)) {
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int resetPassword(String phone, String captcha, String newPassword) {
        if (userMapper.selectByPhone(phone)){
            Boolean isVerify = captchaService.verifyCaptcha(phone,captcha);
            if (isVerify){
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                int isSuccess = userMapper.resetPassword(phone,hashedPassword);
                if (isSuccess > 0){
                    return 0;//修改成功
                }else {
                    return 1;//修改失败
                }
            }else {
                return -1;//验证码错误
            }
        }else {
            return -2;//手机号未注册
        }

    }

    @Override
    public User getUserInfo(int userId) {
        return userMapper.getUserInfo(userId);
    }

    @Override
    public int updateUserInfo(Integer id, UserUpdateDTO updateDTO) {
        Date currentDate = new Date(); // 获取当前日期时间
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        User user = new User(id, updateDTO.getName(), updateDTO.getDescription(), updateDTO.getLocation(), currentTimestamp);
        int isSuccess = userMapper.updateUserInfo(user);
        if (isSuccess > 0){
            return 0;//修改成功
        }else {
            return -1;//修改失败
        }
    }

    @Override
    public PageInfo<User> query(UserQueryDTO userQueryDTO, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(userMapper.query(userQueryDTO));
    }

    @Override
    public int banUser(int userId, int updateId) {
        Date currentDate = new Date(); // 获取当前日期时间
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        int isSuccess = userMapper.banUser(userId,updateId, currentTimestamp);
        if (isSuccess > 0){
            return 0;//封号
        }else {
            return -1;//封号失败
        }
    }

    @Override
    public int unbanUser(int userId, Integer id) {
        Date currentDate = new Date(); // 获取当前日期时间
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        int isSuccess = userMapper.unbanUser(userId, id, currentTimestamp);
        if (isSuccess > 0){
            return 0;//解封
        }else {
            return -1;//解封失败
        }
    }


    @Override
    @Transactional
    public int updatePhone(Integer userId, String oldPhone, String newPhone, String captcha) {
        Boolean isValid = captchaService.verifyCaptcha(newPhone, captcha);
        if (isValid) {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            int isSuccess = userMapper.updatePhone(userId, oldPhone, newPhone, currentTimestamp);
            if (isSuccess > 0) {
                return 0;//修改成功
            } else {
                return -1;//修改失败
            }
        } else {
            return -2;//验证码错误
        }
    }


}
