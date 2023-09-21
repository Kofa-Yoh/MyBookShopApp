package com.example.MyBookShopApp.author;

import com.example.MyBookShopApp.books.books.Book;
import jakarta.persistence.*;

@Entity
public class Book2Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private byte sortIndex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public byte getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(byte sortIndex) {
        this.sortIndex = sortIndex;
    }
}
