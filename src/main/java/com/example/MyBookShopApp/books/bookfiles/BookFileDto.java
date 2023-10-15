package com.example.MyBookShopApp.books.bookfiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookFileDto {

    private Integer id;
    private String hash;
    private String extension;
    private String path;
}
