package com.aiming.low.forum_post_service.aop;

import com.aiming.low.forum_db_log_service.entity.DbLog;
import com.aiming.low.forum_db_log_service.entity.ImmutableDbLog;
import com.aiming.low.forum_post_service.annotation.DbLogger;
import com.aiming.low.forum_post_service.mq.KafkaDbLogProducer;
import com.aiming.low.forum_post_service.util.HttpContextUtils;
import com.aiming.low.forum_post_service.util.WebUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @ClassName DbLogAspect
 * @Description 注解@DbLogger的切面类
 * @Author aiminglow
 */
@Aspect
@Component
public class DbLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbLogAspect.class);
    private final KafkaDbLogProducer kafkaDbLogProducer;

    public DbLogAspect(KafkaDbLogProducer kafkaDbLogProducer) {
        this.kafkaDbLogProducer = kafkaDbLogProducer;
    }

    @Pointcut("@annotation(com.aiming.low.forum_post_service.annotation.DbLogger)")
    public void pointcut() { }

    @Around("pointcut()")
    public Object aroundDbLogger(ProceedingJoinPoint point) throws Exception{
        long beginTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = point.proceed();
            // 日志输出内容
            LOGGER.info("something");
        } catch (Throwable e) {
            // 记录发生异常的交易的日志
            long failExecuteTime = System.currentTimeMillis() - beginTime;
            failLog(point, failExecuteTime, e.getMessage());
            /**
             * 按照一般的处理方法这里可以throw一个自定义的Exception，然后再实现一个@AfterThrowing的advice来处理这里抛出的异常
             * 处理异常的过程就是把执行的方法，参数等等各种信息用logger.error()规范化地打印出来
             * 详见：https://stackoverflow.com/questions/24797157/exception-handling-through-spring-aop-aspectj
             * todo 自定义一个Exception，在这个位置抛出
             */
        }
        // 记录正常结束的交易的日志
        long executeTime = System.currentTimeMillis() - beginTime;
        successLog(point, executeTime);

        return result;
    }

    private ImmutableDbLog getDbLog(ProceedingJoinPoint point, long executeTime) throws Exception{
        ImmutableDbLog.Builder builder = ImmutableDbLog.builder();
        builder.executeTime((short) executeTime).createTime(new Date());

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 根据@DbLogger注解上包含的用户指定的配置进行处理
        // 目前@DbLogger只有一个“指定功能名称”的配置
        DbLogger logAnnotation = method.getAnnotation(DbLogger.class);
        if (logAnnotation != null) {
            builder.optName(logAnnotation.value());
        }

        // 类名和方法名
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        builder.method(className + "." + methodName);

        /**
         * 下面的这段功能会拼接方法的参数名和参数值，形成"param1=arg1, param2=arg2, ..."的字符串存储下来
         * 但是如果遇到“博客正文”之类太长的参数内容，存储在log里面浪费空间。
         * todo 之后需要在@DbLogger的注解里添加更多的配置，使得开发者可以指定存储参数和值的白名单和黑名单。
         */
        // 获得执行的方法的参数值
        Object[] args = point.getArgs();
        // 获得执行的方法的参数名
        LocalVariableTableParameterNameDiscoverer var = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = var.getParameterNames(method);
        if (args.length != 0 && paramNames.length != 0) {
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                // 参数名不可能为null，但是传入的参数是有可能为null的，所以需要判断一下
                String argString = args[i] != null ? args[i].toString() : "null";
                // 形成"param1=arg1, param2=arg2, ..."的字符串
                params.append(paramNames[i] + "=" + argString + ", ");
            }
            // 去掉最后多余的", "两个字符
            if (params.length() >= 2) {
                params.delete(params.length() - 2, params.length());
            }
            builder.params(params.toString());
        }

        // 获取uri，用户IP，user agent
        builder.requestUri(WebUtils.getUri())
                .ip(WebUtils.getClientIp())
                .userAgent(WebUtils.getUserAgent());

        // 获取session中存储的userId，需要用户登录才能获得这个字段
        // 如果未来接入shiro之类的角色权限管理系统，那么获得用户id的方法则需要改变
        HttpSession session = HttpContextUtils.getSession();
        Long userId = (long) session.getAttribute("userId");
        builder.userId(userId);

        return builder.build();
    }

    // 记录正常结束的交易的日志
    private void successLog(ProceedingJoinPoint point, long executeTime) throws Exception{
        ImmutableDbLog dbLog = getDbLog(point, executeTime);
        // 设置操作状态为：1-成功
        dbLog = dbLog.withLogStatus(DbLog.LogStatus.SUCCESS);

        kafkaDbLogProducer.sendDbLog(dbLog);
    }

    // 记录发生异常的交易的日志
    private void failLog(ProceedingJoinPoint point, long executeTime, String errorMsg) throws Exception{
        ImmutableDbLog.Builder builder = getDbLog(point, executeTime).builder();
        // 设置操作状态为：-1-失败
        builder.logStatus(DbLog.LogStatus.FAILURE).errorMsg(errorMsg);

        kafkaDbLogProducer.sendDbLog(builder.build());
    }
}
