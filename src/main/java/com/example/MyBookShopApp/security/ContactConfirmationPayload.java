package com.example.MyBookShopApp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContactConfirmationPayload {

    private String contact;
    private String code;
}
