package com.champion.readingstory.vo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 17:00
 */
@Data
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = -7894684391657703161L;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

}
