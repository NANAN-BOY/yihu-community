package com.yihu.service;

import com.yihu.entiy.User;

import java.util.List;

public interface UserService {
    User getUserByPhone(int phopne);

    List<User> getAll();

    int register(String userName, String password, String phoneNumber, String captcha);
}
