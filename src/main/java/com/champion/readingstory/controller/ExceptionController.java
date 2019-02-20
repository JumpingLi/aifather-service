package com.champion.readingstory.controller;

import com.champion.readingstory.exception.BusinessException;
import com.champion.readingstory.vo.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理全局异常
 *
 * @author jpli3
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleErrorException(Exception e) {
        log.error("sever error: ", e);
        return CommonResponse.buildFailure(HttpStatus.INTERNAL_SERVER_ERROR+"", "系统业务高峰,请稍后再试!");
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleBusinessException(BusinessException e) {
        log.error("business error: ", e.getMessage());
        return CommonResponse.buildFailure(HttpStatus.INTERNAL_SERVER_ERROR+"", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Object handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder paramErrors = new StringBuilder();
        for (ConstraintViolation<?> item : violations) {
            paramErrors = paramErrors.append(item.getMessage());
        }
        log.error("request param error: {}", paramErrors.toString());
        return CommonResponse.buildFailure(HttpStatus.BAD_REQUEST+"", paramErrors.toString());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Object handleWebExchangeBindException(WebExchangeBindException e) {
        //获取参数校验错误集合
        List<FieldError> fieldErrors = e.getFieldErrors();
        //格式化以提供友好的错误提示
        String data = String.format("参数校验错误: %s", fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";")));
        log.error("request body error: {}", data);
        //参数校验失败响应失败个数及原因
        return CommonResponse.buildFailure(e.getStatus().value()+"", data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object bindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errMsg = new StringBuilder("参数校验失败: ");
        List<String> errMsgs = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        errMsg.append(String.join(",",errMsgs));
        return CommonResponse.buildFailure(errMsg.toString());
    }
}
