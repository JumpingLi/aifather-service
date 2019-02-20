package com.champion.readingstory.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 20:20
 */
@Data
@Builder
public class MyPageVO {
    private Boolean isBind;
    private Integer voiceCount;
    private Integer userStoryCount;
}
