package com.example.MyBookShopApp.books.books;

import java.util.List;

public class BooksPageDto {

    private Integer count;

    private List<BookDto> books;

    public BooksPageDto(List<BookDto> books) {
        this.books = books;
        this.count = books.size();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<BookDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookDto> books) {
        this.books = books;
    }
}
