package com.champion.readingstory.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 11:42
 */
@Data
public class PTTSRequest {
    @NotBlank(message = "发音人不能为空!")
    private String vcn;
    @NotBlank(message = "合成文字内容不能为空!")
    private String content;
    @NotNull(message = "故事Id不能为空!")
    private Integer storyId;
}
