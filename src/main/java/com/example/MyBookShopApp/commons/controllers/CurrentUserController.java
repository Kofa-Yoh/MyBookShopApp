package com.example.MyBookShopApp.commons.controllers;

import com.example.MyBookShopApp.security.UserDto;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class CurrentUserController {

    private final BookStoreUserRegister bookStoreUserRegister;

    @ModelAttribute("curUsr")
    public UserDto getCurrentUser() {
        return bookStoreUserRegister.getCurrentUserDto();
    }
}
