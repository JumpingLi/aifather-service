package com.champion.readingstory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 19:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryVO {
    private Integer id;
    private String title;
    private String imageUrl;
    private String updateAt;
}
