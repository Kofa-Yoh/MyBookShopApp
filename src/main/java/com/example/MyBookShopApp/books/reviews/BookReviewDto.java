package com.example.MyBookShopApp.books.reviews;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewDto {

    private Integer id;
    private String userName;
    private String review;
    private String createTime;
    private Integer likesCount;
    private Integer dislikesCount;
    private Integer rating;

    private Byte userLike;

    public static int compareByRatingSortedAlgorithm(BookReviewDto r1, BookReviewDto r2) {
        return Integer.compare(r1.likesCount - r1.dislikesCount, r2.likesCount - r2.dislikesCount);
    }
}
