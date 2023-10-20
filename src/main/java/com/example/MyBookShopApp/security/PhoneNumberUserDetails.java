package com.example.MyBookShopApp.security;

import org.springframework.security.core.userdetails.UserDetails;

public class PhoneNumberUserDetails extends BookStoreUserDetails {
    public PhoneNumberUserDetails(BookStoreUser bookStoreUser) {
        super(bookStoreUser);
    }

    @Override
    public String getUsername() {
        return getBookStoreUser().getPhone();
    }
}
