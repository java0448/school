package ru.t1.school.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ru.t1.school.controller.TaskController.*(..))")
    public void logBefore() {
        logger.info("A request is about to be processed.");
    }

    @AfterThrowing(pointcut = "execution(* ru.t1.school.controller.TaskController.*(..))", throwing = "exception")
    public void logAfterThrowing(Exception exception) {
        logger.error("An exception occurred: {}", exception.getMessage());
    }

    @AfterReturning(pointcut = "execution(* ru.t1.school.controller.TaskController.*(..))", returning = "result")
    public void logAfterReturning(Object result) {
        logger.info("A request was successfully processed. Result: {}", result);
    }

    @Around("execution(* ru.t1.school.controller.TaskController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Time taken by {} is {} ms", joinPoint.getSignature(), timeTaken);
        return result;
    }
}