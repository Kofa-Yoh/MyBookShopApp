package com.example.MyBookShopApp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProfileForm {

    private String name;
    private String mail;
    private String phone;
    private String password;
    private String passwordReply;
}
