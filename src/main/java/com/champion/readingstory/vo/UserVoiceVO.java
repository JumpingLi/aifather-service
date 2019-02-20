package com.champion.readingstory.vo;

import lombok.Data;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 14:17
 */
@Data
public class UserVoiceVO {
    private String voice_id;
    private String vcn;
    private String app_id;
    private String third_user_id;
    private String phone_number;
    private String voice_name;
    private Integer train_ret;
    private String listen_url;
    private Long updated_time;
    private Long created_time;
    private String batch_id;
}
