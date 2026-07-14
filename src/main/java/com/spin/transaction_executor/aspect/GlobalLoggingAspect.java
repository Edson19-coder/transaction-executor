package com.spin.transaction_executor.aspect;

import com.spin.transaction_executor.domain.exception.GlobalException;
import com.spin.transaction_executor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class GlobalLoggingAspect {
    @Pointcut("execution(* com.spin.transaction_executor.service..*.*(..))")
    public void allServiceMethods() {}

    @Before("allServiceMethods()")
    public void logInput(JoinPoint joinPoint) {
        MDC.put(Constants.REQUEST_ID, UUID.randomUUID().toString());
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info(Constants.INPUT, args[0]);
        }
    }

    @AfterReturning(pointcut = "allServiceMethods()", returning = "result")
    public void logOutput(Object result) {
        if (result != null) {
            log.info(Constants.OUTPUT, result);
        }
    }

    @AfterThrowing(pointcut = "allServiceMethods()", throwing = "e")
    public void logExceptionOutput(Exception e) {
        if (e instanceof GlobalException statefulEx && statefulEx.getData() != null) {
            log.info(Constants.OUTPUT, statefulEx.getData());
        } else {
            log.error("Service execution failed unexpectedly: {}", e.getMessage(), e);
        }
    }

    @After("allServiceMethods()")
    public void afterService() {
        MDC.remove(Constants.REQUEST_ID);
    }
}
