package org.hanghae.markethub.global.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTraceAop {

    @Pointcut("execution(* org.hanghae.markethub.domain..*(..)) && !within(org.hanghae.markethub.domain.purchase.controller.PaymentController)")
    public void targetMethods() {}

    @Around("targetMethods()")
    public Object execute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println("Time : " + (end - start));
        return result;
    }





}

