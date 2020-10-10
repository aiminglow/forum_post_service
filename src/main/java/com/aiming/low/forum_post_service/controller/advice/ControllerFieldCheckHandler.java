package com.aiming.low.forum_post_service.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Set;

/**
 * @ClassName ControllerFieldCheckHandler
 * @Description 切面类，用来切入controller的方法，对字段校验失败后的ConstraintViolationException进行捕获，然后返回response。
 * 所以说Advice的类型为Exception Advice。
 * @Author aiminglow
 */
@ControllerAdvice
@Component
public class ControllerFieldCheckHandler {
    /**
     * @Description: hibernate 参数校验出错会抛出 ConstraintViolationException 异常
    在此方法中处理，将错误信息放在response中返回。
     * @Param:
     * @return:
     * @Author: aiminglow
     * todo 目前没有捕获MethodArgumentTypeMismatchException的异常
     * 如果controller的方法需要int参数，但是提供的是String参数的时候，就会出现上面这种异常
     *
     * 使用javax.validation.constraint包下的注解进行参数校验，就会抛出ValidationException异常，也就是下面handle方法处理的异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public HashMap<String, String> handle(ValidationException exception){
        StringBuilder errorInfo = new StringBuilder();
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException exc = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violationSet = exc.getConstraintViolations();

            for (ConstraintViolation<?> item: violationSet) {
                errorInfo = errorInfo.append(item.getMessage() + "|");
            }
        }
        if (!errorInfo.toString().equals(""))
            errorInfo.deleteCharAt(errorInfo.length() - 1);
        HashMap<String, String> msgMap = new HashMap<>();
        msgMap.put("error-msg", errorInfo.toString());
        return msgMap;
    }
}
