package com.champion.readingstory.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author jpli3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = -1039287181059560509L;
    private Boolean success;
    private String msg;
    private String code;
    private T data;


    public static <T> CommonResponse<T> buildSuccess() {
        return buildSuccess(null);
    }

    public static <T> CommonResponse<T> buildSuccess(T v) {
        return CommonResponse.<T>builder()
                .success(Boolean.TRUE)
                .msg("success")
                .code("0000")
                .data(v)
                .build();
    }

    public static <T> CommonResponse<T> buildSuccess(T v,String msg) {
        return CommonResponse.<T>builder()
                .success(Boolean.TRUE)
                .msg(msg)
                .code("0000")
                .data(v)
                .build();
    }

    public static <T> CommonResponse<T> buildFailure(String code, String msg) {
        return CommonResponse.<T>builder()
                .success(Boolean.FALSE)
                .msg(msg)
                .code(code)
                .build();
    }

    public static <T> CommonResponse<T> buildFailure(String msg) {
        return CommonResponse.<T>builder()
                .success(Boolean.FALSE)
                .msg(msg)
                .code("9999")
                .build();
    }
}
