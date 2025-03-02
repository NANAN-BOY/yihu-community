package com.yihu.service.impl;

import com.yihu.entiy.User;
import com.yihu.mapper.UserMapper;
import com.yihu.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public User getUserByPhone(int phopne) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return userMapper.selectAll();
    }
}
