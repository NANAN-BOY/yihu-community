package com.yihu.mapper;

import com.yihu.dto.UserQueryDTO;
import com.yihu.entity.User;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    int register(User user);//注册

    User login(@Param("phone") String phone);//登录

    Boolean selectByPhone(@Param("phone") String phone);//通过手机号查询是否已注册

    int resetPassword(@Param("phone") String phone,@Param("newPassword") String newPassword);//重置密码

    User getUserInfo(@Param("userId") Integer userId);

    int updateUserInfo(User user);

    List<User> query(UserQueryDTO userQueryDTO);

    int banUser(@Param("userId") int userId,@Param("updateId") Integer updateId,@Param("updateTime") Date updateTime);

    int unbanUser(@Param("userId")int userId,@Param("updateId") Integer id,@Param("updateTime") Timestamp currentTimestamp);
}
