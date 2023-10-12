package com.example.MyBookShopApp.commons.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Pointcut(value = "execution(* com.example.MyBookShopApp.restapi.BooksRestApiController.*())")
    public void allApiMethodsWithoutParameters() {
    }

    @Around(value = "allApiMethodsWithoutParameters()")
    public Object afterApiMethodsWithoutParameters(ProceedingJoinPoint proceedingJoinPoint) {
        logger.info("---------- Method " + proceedingJoinPoint.getSignature().getName() + " begins ----------");
        Object returnValue = null;
        try {
            returnValue = proceedingJoinPoint.proceed();
            logger.info(returnValue.toString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        logger.info("---------- Method " + proceedingJoinPoint.getSignature().getName() + " ends ----------");
        return returnValue;
    }

    @Pointcut(value = "execution(* com.example.MyBookShopApp.restapi.BooksRestApiController.*(..))")
    public void allApiMethodsWithParameters() {
    }

    @Before(value = "args(param) && allApiMethodsWithParameters()")
    public void afterApiMethodsWithOneParameter(JoinPoint joinPoint, Object param) {
        logger.info("Method " + joinPoint.getSignature().getName() + " with arg: " + param);
    }

    @Before(value = "args(one, two) && allApiMethodsWithParameters()")
    public void afterApiMethodsWithTwoParameters(JoinPoint joinPoint, Object one, Object two) {
        logger.info("Method " + joinPoint.getSignature().getName() + " with args: " + one + ", " + two);
    }

    @AfterThrowing(value = "allApiMethodsWithParameters()", throwing = "ex")
    public void afterApiMethodsThrowingException(JoinPoint joinPoint, Exception ex) {
        logger.info("Method " + joinPoint.getSignature().getName() + " throws exception " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
    }
}
