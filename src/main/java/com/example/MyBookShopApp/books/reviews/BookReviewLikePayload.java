package com.example.MyBookShopApp.books.reviews;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewLikePayload {

    Integer reviewId;
    Byte value;
}
