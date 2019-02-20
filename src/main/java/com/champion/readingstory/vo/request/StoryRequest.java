package com.champion.readingstory.vo.request;

import com.champion.readingstory.dao.enumeration.AgeSection;
import com.champion.readingstory.dao.enumeration.StoryCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 17:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StoryRequest extends BaseRequest {
    private Integer id;
    private AgeSection ageSection;
    private StoryCategory category;
}
