package com.example.MyBookShopApp.books.books;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookDto {
    private Integer id;
    private String slug;
    private Date pubDate;
    private Integer isBestseller;
    private String title;
    private String image;
    private String description;
    private String authors;
    private Integer discount;
    private Integer rating;
    private Integer price;
    private Integer discountPrice;
}
