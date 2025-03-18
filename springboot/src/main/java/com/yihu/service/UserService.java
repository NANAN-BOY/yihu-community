package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.UserQueryDTO;
import com.yihu.dto.UserUpdateDTO;
import com.yihu.entity.User;

import java.util.List;

public interface UserService {
    User getUserByPhone(String phone);

    List<User> getAll();

    int register(String userName, String password, String phoneNumber, String captcha, String location);

    User login(String phone, String password);

    int resetPassword(String phone, String captcha, String newPassword);

    User getUserInfo(int userId);

    int updateUserInfo(Integer id, UserUpdateDTO updateDTO);

    PageInfo<User> query(UserQueryDTO userQueryDTO, int pageNum, int pageSize);

    int banUser(int userId, int updateId);

    int unbanUser(int userId, Integer id);
}
