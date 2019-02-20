package com.champion.readingstory.controller;

import com.champion.readingstory.exception.BusinessException;
import com.champion.readingstory.vo.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 处理被 aop切面环绕的异常
 *
 * @author jpli3
 */
@Slf4j
@RestController
@Aspect
public class AopController {
    @Around(value = "execution(public com.champion.readingstory.vo.response.CommonResponse *(..))")
    private CommonResponse<?> handlerControllerMethod(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        CommonResponse<?> result;
        try {
            result = (CommonResponse<?>) pjp.proceed();
            log.info(pjp.getSignature() + " used time: " + (System.currentTimeMillis() - startTime));
        } catch (Throwable e) {
            result = handlerException(pjp, e);
        }
        return result;
    }

    private CommonResponse<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
        CommonResponse<?> result = CommonResponse.buildFailure("");
        // 参数校验异常
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
            StringBuilder stringBuilder = new StringBuilder();
            for (ConstraintViolation<?> item : violations) {
                stringBuilder = stringBuilder.append(item.getMessage());
            }
            log.error(pjp.getSignature() + ", error: {}", stringBuilder.toString());
            result.setMsg(stringBuilder.toString());
            result.setCode(HttpStatus.BAD_REQUEST.value()+"");
        } else if (e instanceof BusinessException) {
            // 具体业务异常
            log.error(pjp.getSignature() + ", error: ", e);
            result.setMsg(e.getMessage());
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()+"");
        } else if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException manve = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = manve.getBindingResult();
            StringBuilder errMsg = new StringBuilder("参数校验失败: ");
            List<String> errMsgs = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            errMsg.append(String.join(",",errMsgs));
            result.setMsg(errMsg.toString());
            result.setCode(HttpStatus.BAD_REQUEST.value()+"");
        }else {
            // 其它细分异常
            log.error(pjp.getSignature() + ", error: ", e);
            result.setMsg("当前系统业务忙,请稍候再试!");
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()+"");
        }
        return result;
    }
}
