package com.champion.readingstory.service.impl;

import com.champion.readingstory.dao.BaseMapper;
import com.champion.readingstory.dao.entity.Story;
import com.champion.readingstory.dao.mapper.StoryMapper;
import com.champion.readingstory.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 16:52
 */
@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryMapper storyMapper;

    @Override
    public BaseMapper<Story> getMapper() {
        return storyMapper;
    }
}
