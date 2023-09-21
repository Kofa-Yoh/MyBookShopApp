package com.example.MyBookShopApp.books.usersbooks;

public enum Book2UserTypeDto {
    KEPT("KEPT"), CART("CART"), PAID("PAID"), ARCHIVED("ARCHIVED");

    private String value;

    Book2UserTypeDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
