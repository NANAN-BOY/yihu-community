package com.yihu.service;

import com.yihu.entity.User;

import java.util.List;

public interface UserService {
    User getUserByPhone(int phone);

    List<User> getAll();

    int register(String userName, String password, String phoneNumber, String captcha, String location);

    User login(String phone, String password);

    int resetPassword(String phone, String captcha, String newPassword);
}
