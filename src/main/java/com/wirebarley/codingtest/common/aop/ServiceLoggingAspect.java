package com.wirebarley.codingtest.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("execution(* com.wirebarley.codingtest..service..*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // DEBUG: 서비스 진입 로그
        if (log.isDebugEnabled()) {
            log.debug(
                    "[ServiceStart] {}.{} args={}",
                    className,
                    methodName,
                    Arrays.toString(joinPoint.getArgs())
            );
        }

        try {
            Object result = joinPoint.proceed();

            long elapsed = System.currentTimeMillis() - startTime;

            // DEBUG: 정상 종료 로그
            if (log.isDebugEnabled()) {
                log.debug(
                        "[ServiceEnd] {}.{} elapsed={}ms",
                        className,
                        methodName,
                        elapsed
                );
            }

            return result;

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;

            // ERROR: 예외 로그 (공통)
            log.error(
                    "[ServiceException] {}.{} elapsed={}ms message={}",
                    className,
                    methodName,
                    elapsed,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }
}
