package com.champion.readingstory.service.impl;

import com.champion.readingstory.dao.BaseMapper;
import com.champion.readingstory.dao.entity.User;
import com.champion.readingstory.dao.mapper.UserMapper;
import com.champion.readingstory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 10:19
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public BaseMapper<User> getMapper() {
        return userMapper;
    }
}
