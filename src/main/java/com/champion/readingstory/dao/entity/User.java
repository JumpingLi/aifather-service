package com.champion.readingstory.dao.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@Table(name = "`user`")
public class User {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`open_id`")
    private String openId;

    @Column(name = "`is_bind`")
    private Boolean isBind;

    @Column(name = "`phone_no`")
    private String phoneNo;

    @Column(name = "`create_at`")
    private Timestamp createAt;

    @Column(name = "`update_at`")
    private Timestamp updateAt;

}