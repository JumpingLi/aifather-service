package com.champion.readingstory.constant;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 10:43
 */
public enum LSReturnCode {
    SUCCESS("000000","成功"),
    IP_NOT_AUTH("100001","请求ip未授权"),
    SIGN_FAILED("100002","sign校验失败"),
    TOKEN_NOT_AUTH("100003","请求token未授权"),
    UNKNOW("999999","未知错误"),
    PHONE_NO_BOUND("200004","该手机号已绑定"),
    USER_BOUND("200003","该用户已绑定");

    private String code;
    private String msg;

    LSReturnCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
