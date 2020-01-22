package com.ryan.temporarycyclechange;

import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

/**
 * 
 * @author rsapl00
 */
@Aspect
@Configuration
public class LoggingAspect {

    final private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)"
            + " || within(@org.springframework.stereotype.Service *)"
            + " || within(@org.springframework.web.bind.annotation.RestController *)"
            + " || within(@javax.validation.Valid *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.ryan.temporarycyclechange..*)" + " || within(com.ryan.temporarycyclechange.service..*)"
            + " || within(com.ryan.temporarycyclechange.controller..*)" + " || within(com.ryan.temporarycyclechange.domain..*)"
            + " || within(com.ryan.temporarycyclechange.repository..*)" + " || within(com.ryan.temporarycyclechange.security..*)"
            + " || within(com.ryan.temporarycyclechange.util..*)" + " || within(com.ryan.temporarycyclechange.validation..*)"
            + " || within(com.ryan.temporarycyclechange.service.resource.mail..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }

    // AOP expression for which methods shall be intercepted
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        // Get intercepted method details
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();

        // Measure method execution time
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        // Log method execution time
        if (logger.isDebugEnabled()) {
            logger.debug("Execution time of " + className + "." + methodName + " :: " + stopWatch.getTotalTimeMillis()
                    + " ms");
        }

        return result;
    }

}