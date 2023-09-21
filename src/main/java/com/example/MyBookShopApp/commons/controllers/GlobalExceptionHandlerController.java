package com.example.MyBookShopApp.commons.controllers;

import com.example.MyBookShopApp.errs.EmptySearchException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    public  String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public  String handleUsernameNotFoundException(UsernameNotFoundException e){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "redirect:/signin";
    }
}
