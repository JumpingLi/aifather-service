package com.champion.readingstory.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: JiangPing Li
 * @date: 2018-09-19 9:50
 */
@Data
public class WXACodeRequest {
    @NotBlank(message = "场景参数不能为空!")
    private String scene;
    @NotBlank(message = "页面参数不能为空!")
    private String page;
}
