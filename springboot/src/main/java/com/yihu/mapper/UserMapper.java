package com.yihu.mapper;

import com.yihu.entity.User;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    int register(User user);//注册
}
