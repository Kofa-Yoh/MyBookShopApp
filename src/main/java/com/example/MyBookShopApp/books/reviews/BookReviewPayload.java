package com.example.MyBookShopApp.books.reviews;

public class BookReviewPayload {

    String bookId;
    String text;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
