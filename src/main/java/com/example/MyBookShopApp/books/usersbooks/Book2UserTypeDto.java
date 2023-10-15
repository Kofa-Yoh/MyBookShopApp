package com.example.MyBookShopApp.books.usersbooks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Book2UserTypeDto {
    KEPT("KEPT"), CART("CART"), PAID("PAID"), ARCHIVED("ARCHIVED");

    private String value;
}
