package com.champion.readingstory.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 19:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserStoryRequest extends BaseRequest {
    private Integer userId;

}
