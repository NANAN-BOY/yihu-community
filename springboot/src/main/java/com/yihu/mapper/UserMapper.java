package com.yihu.mapper;

import com.yihu.entiy.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    int register(@Param("userName") String userName, @Param("password") String password, @Param("phoneNumber") String phoneNumber);//注册
}
