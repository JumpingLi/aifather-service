package com.champion.readingstory.dao.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@Table(name = "`user_story`")
public class UserStory {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "`user_id`")
    private Integer userId;

    /**
     * 故事id
     */
    @Column(name = "`story_id`")
    private Integer storyId;

    /**
     * 已读次数
     */
    @Column(name = "`reading_times`")
    private Byte readingTimes;

    /**
     * 创建时间
     */
    @Column(name = "`create_at`")
    private Timestamp createAt;

    @Column(name = "`update_at`")
    private Timestamp updateAt;
}