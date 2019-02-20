package com.champion.readingstory.vo.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: JiangPing Li
 * @date: 2018-08-14 14:59
 */
@Builder
@Data
public class SmsResponse implements Serializable {
    private static final long serialVersionUID = 8837371631589346066L;
    private String returnCode;
    private String returnDesc;
}
