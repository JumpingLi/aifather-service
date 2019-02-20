package com.champion.readingstory.service;

import com.champion.readingstory.dao.BaseMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 16:31
 */
public interface BaseService<T> {

    BaseMapper<T> getMapper();

    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    default int insertSelective(T entity){
        return getMapper().insertSelective(entity);
    }

    default int updateByPrimaryKeySelective(T entiry){
        return getMapper().updateByPrimaryKeySelective(entiry);
    }

    default T selectByKey(Object key){
        return getMapper().selectByPrimaryKey(key);
    }

    default T selectOne(T entity){
        return getMapper().selectOne(entity);
    }


    default List<T> selectByPage(T entity){
        return getMapper().select(entity);
    }

    default List<T> selectByExample(Example example){
        return getMapper().selectByExample(example);
    }
    default T selectOneByExample(Example example){
        return getMapper().selectOneByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    default int updateByExampleSelective(T entity,Example example){
        return getMapper().updateByExampleSelective(entity,example);
    }
}
