package com.champion.readingstory.service.impl;

import com.champion.readingstory.dao.BaseMapper;
import com.champion.readingstory.dao.entity.UserStory;
import com.champion.readingstory.dao.mapper.UserStoryMapper;
import com.champion.readingstory.service.UserStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 19:11
 */
@Service
public class UserStoryServiceImpl implements UserStoryService {
    @Autowired
    private UserStoryMapper userStoryMapper;
    @Override
    public BaseMapper<UserStory> getMapper() {
        return userStoryMapper;
    }
}
