package com.aiming.low.forum_post_service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将用户操作记录在mysql数据库的注解，加在controller层的方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbLogger {
    // 用户请求的功能的名称，比如“添加博客”
    String value() default "";
}
