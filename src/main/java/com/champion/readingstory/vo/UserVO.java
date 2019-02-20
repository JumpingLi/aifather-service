package com.champion.readingstory.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: JiangPing Li
 * @date: 2018-09-06 11:17
 */
@Data
@Builder
public class UserVO {
    private Boolean isBind;
    private String phoneNo;
    private String openId;
}
