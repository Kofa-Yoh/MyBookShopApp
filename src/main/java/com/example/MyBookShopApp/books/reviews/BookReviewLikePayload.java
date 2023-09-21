package com.example.MyBookShopApp.books.reviews;

public class BookReviewLikePayload {

    Integer reviewid;

    Byte value;

    public Integer getReviewid() {
        return reviewid;
    }

    public void setReviewid(Integer reviewid) {
        this.reviewid = reviewid;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }
}
