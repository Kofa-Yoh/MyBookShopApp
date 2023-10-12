package com.example.MyBookShopApp.commons.aspects;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.usersbooks.Book2UserTypeDto;
import com.example.MyBookShopApp.security.BookStoreUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Pointcut(value = "@annotation(Loggable)")
    public void loggableMethod() {
    }

    @Pointcut(value = "execution(* com.example.MyBookShopApp.restapi.BooksRestApiController.*())")
    public void allApiMethodsWithoutParameters() {
    }

    @Pointcut(value = "execution(* com.example.MyBookShopApp.restapi.BooksRestApiController.*(..))")
    public void allApiMethodsWithParameters() {
    }

    @Before(value = "loggableMethod()")
    public void beforeLoggableMethod(JoinPoint joinPoint) {
        logger.info("Method " + joinPoint.getSignature().getName());
        Arrays.stream(joinPoint.getArgs())
                .map(Objects::toString)
                .forEach(str -> logger.info(str));
    }

    @After(value = "args(book, user, status, time) && loggableMethod()")
    public void afterAddBook2User(Book book, BookStoreUser user, String status, LocalDateTime time) {
        logger.info("User " + user.getEmail() + " add book \"" + book.getTitle() + "\" to the " + status);
    }

    @After(value = "args(book, user, linkType) && loggableMethod()")
    public void afterAddBook2User(Book book, BookStoreUser user, Book2UserTypeDto linkType) {
        logger.info("User " + user.getEmail() + " remove book \"" + book.getTitle() + "\" from " + linkType.getValue());
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
