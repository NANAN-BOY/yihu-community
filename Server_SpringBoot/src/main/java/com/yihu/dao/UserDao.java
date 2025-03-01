package com.yihu.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    @Select("select * from user where phoneNumber=#{phoneNumber}")
    Boolean isPhoneNumberExist(@Param("phoneNumber") String phoneNumber);

    @Insert("insert into 'user'(userName,password,phoneNumber) values(#{userName},#{password},#{phoneNumber})")
    Boolean resigister(@Param("userName") String userName,@Param("password") String password,@Param("phoneNumber") String phoneNumber);
}
