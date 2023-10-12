package com.example.MyBookShopApp.commons.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.logging.Logger;

@Aspect
@Component
public class MethodExecTrackerAspect {

    private Long durationMills;
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Before(value = "execution(* com.example.MyBookShopApp.books.books.MainPageController.*(..))")
    public void beforeDurationTrackingAdvice(JoinPoint joinPoint) {
        durationMills = new Date().getTime();
        logger.info("Method " + joinPoint.toShortString() + " begin");
    }

    @Pointcut(value = "within(com.example.MyBookShopApp.books.books.MainPageController)")
    public void allMainPageControllerMethods() {
    }

    @After(value = "allMainPageControllerMethods()")
    public void afterDurationTrackingAdvice(JoinPoint joinPoint) {
        durationMills = new Date().getTime() - durationMills;
        logger.info("Method " + joinPoint.getSignature().getName() + " end in " + durationMills + " mills");
    }

    @Pointcut(value = "execution(* com.example.MyBookShopApp.books.books.BookService.getPageOfPopularBooks(..))")
    public void popularBooksListMethod() {
    }

    @After(value = "args(offset, limit) && popularBooksListMethod()")
    public void afterPopularBooksTrackingAdvice(JoinPoint joinPoint, Integer offset, Integer limit) {
        logger.info("Method " + joinPoint.getSignature().getName() + " with args offset & limit: " + offset + " " + limit);
    }

    @Pointcut(value = "execution(* com.example.MyBookShopApp.books.books.BooksController.saveNewBookImage(..))")
    public void saveNewBookImageMethod() {
    }

    @AfterReturning(pointcut = "saveNewBookImageMethod()")
    public void afterSaveImageThrowException(JoinPoint joinPoint) {
        logger.info("Method " + joinPoint.getSignature().getName() + " doesn't throw exception");
    }

    @AfterThrowing(value = "args(file,slug) && saveNewBookImageMethod()", throwing = "ex")
    public void afterSaveImageThrowException(JoinPoint joinPoint, MultipartFile file, String slug, Exception ex) {
        logger.info("Method " + joinPoint.getSignature().getName() + " throws exception: " + ex.getLocalizedMessage());
        logger.info(file.getName() + " " + slug);
    }


}
