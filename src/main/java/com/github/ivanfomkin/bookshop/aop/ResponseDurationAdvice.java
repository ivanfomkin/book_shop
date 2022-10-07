package com.github.ivanfomkin.bookshop.aop;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ResponseDurationAdvice {
    @SuppressWarnings("java:S1186")
    @Pointcut("within(@org.springframework.stereotype.Controller * ) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void allControllers() {
    }

    @Around("allControllers()")
    public Object controllerDurationTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        Throwable throwable = null;
        var startTime = System.currentTimeMillis();
        Object returnedValue = null;
        try {
            returnedValue = joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
        } finally {
            var stopTime = System.currentTimeMillis();
            log.debug("Method {} execution time is {} ms", joinPoint.getSignature().getName(), stopTime - startTime);
        }

        if (throwable != null) {
            throw throwable;
        }
        return returnedValue;
    }

    @Around("@annotation(executionTimeLog)")
    public Object markedLogExecutionTimeAnnotationAround(ProceedingJoinPoint joinPoint, ExecutionTimeLog executionTimeLog) {
        var startTime = System.currentTimeMillis();
        Object returnedValue = null;
        try {
            returnedValue = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        var stopTime = System.currentTimeMillis();
        if (executionTimeLog.withUserInfo()) {
            var email = SecurityContextHolder.getContext().getAuthentication().getName();
            log.debug("Method {} execution time is {} ms, user is {}", joinPoint.getSignature().getName(), stopTime - startTime, email);
        } else {
            log.debug("Method {} execution time is {} ms", joinPoint.getSignature().getName(), stopTime - startTime);
        }
        return returnedValue;
    }
}
