package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "book2user")
public class Book2User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Book2UserType linkType;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookStoreUser user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Book2UserType getLinkType() {
        return linkType;
    }

    public void setLinkType(Book2UserType linkType) {
        this.linkType = linkType;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookStoreUser getUser() {
        return user;
    }

    public void setUser(BookStoreUser user) {
        this.user = user;
    }
}
