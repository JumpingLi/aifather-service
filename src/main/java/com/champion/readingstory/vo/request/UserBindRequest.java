package com.champion.readingstory.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 15:32
 */
@Data
public class UserBindRequest {
    @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "手机号码格式不正确")
    private String phoneNo;
    @NotBlank(message = "验证码不能为空")
    private String code;
}
