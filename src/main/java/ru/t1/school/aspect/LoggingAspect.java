package ru.t1.school.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования выполнения методов контроллера задач.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Логирует сообщение перед выполнением любого метода в TaskController.
     *
     * @param joinPoint точка соединения, представляющая выполнение метода
     */
    @Before("execution(* ru.t1.school.controller.TaskController.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        logger.info("A request is about to be processed. Method: {}. Arguments: {}", methodName, methodArgs);
    }

    /**
     * Логирует исключение, выброшенное любым методом в TaskController.
     *
     * @param exception выброшенное исключение
     */
    @AfterThrowing(pointcut = "execution(* ru.t1.school.controller.TaskController.*(..))", throwing = "exception")
    public void logAfterThrowing(Exception exception) {
        logger.error("An exception occurred: {}", exception.getMessage());
    }

    /**
     * Логирует результат выполнения любого метода в TaskController после его успешного выполнения.
     *
     * @param result результат, возвращенный методом
     */
    @AfterReturning(pointcut = "execution(* ru.t1.school.controller.TaskController.*(..))", returning = "result")
    public void logAfterReturning(Object result) {
        logger.info("A request was successfully processed. Result: {}", result);
    }

    /**
     * Логирует время, затраченное на выполнение любого метода в TaskController.
     *
     * @param joinPoint точка соединения, представляющая выполнение метода
     * @return результат выполнения метода
     * @throws Throwable если возникает ошибка во время выполнения метода
     */
    @Around("execution(* ru.t1.school.controller.TaskController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Time taken by {} is {} ms", joinPoint.getSignature(), timeTaken);
        return result;
    }
}