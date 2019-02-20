package com.champion.readingstory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 16:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoryVO {
    private Integer id;
    private String ageSection;
    private String category;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String content;
    private String createAt;
    private String updateAt;
    private Set<Integer> ids;
}
