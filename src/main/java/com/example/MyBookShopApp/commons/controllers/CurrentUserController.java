package com.example.MyBookShopApp.commons.controllers;

import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.UserDto;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import com.example.MyBookShopApp.user_transactions.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class CurrentUserController {

    private final BookStoreUserRegister bookStoreUserRegister;
    private final PaymentService paymentService;

    @ModelAttribute("curUsr")
    public UserDto getCurrentUser() {
        return bookStoreUserRegister.getCurrentUserDto();
    }

    @ModelAttribute("userBalance")
    public Double getUserBalance() {
        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            return Double.valueOf(0);
        } else {
            return paymentService.getBalance(currentUser.getBookStoreUser());
        }
    }
}
