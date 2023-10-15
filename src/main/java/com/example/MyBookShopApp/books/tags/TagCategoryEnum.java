package com.example.MyBookShopApp.books.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagCategoryEnum {
    XS(0.25), SM(0.5), MD(0.75), LG(1.0);

    private Double percent;
}