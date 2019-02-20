package com.champion.readingstory.dao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author: JiangPing Li
 * @date: 2018-08-16 8:59
 */
public interface BaseMapper<T> extends Mapper<T>,MySqlMapper<T> {

}
