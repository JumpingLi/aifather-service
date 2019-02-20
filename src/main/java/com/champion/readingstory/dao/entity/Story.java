package com.champion.readingstory.dao.entity;

import com.champion.readingstory.dao.enumeration.AgeSection;
import com.champion.readingstory.dao.enumeration.StoryCategory;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@Table(name = "`story`")
public class Story {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 年龄区间
     */
    @Column(name = "`age_section`")
    private AgeSection ageSection;

    /**
     * 故事分类
     */
    @Column(name = "`category`")
    private StoryCategory category;

    @Column(name = "`title`")
    private String title;

    @Column(name = "`subtitle`")
    private String subtitle;

    /**
     * 图片地址
     */
    @Column(name = "`image_url`")
    private String imageUrl;

    @Column(name = "`create_at`")
    private Timestamp createAt;

    @Column(name = "`update_at`")
    private Timestamp updateAt;

    @Column(name = "`content`")
    private String content;
}