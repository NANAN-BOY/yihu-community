package com.yihu.mapper;

import com.yihu.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    int register(User user);//注册

    User login(@Param("phone") String phone);//登录
}
