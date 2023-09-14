package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_assesment")
public class BookAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookStoreUser user;

    @ColumnDefault("0")
    private Byte rate;

    private LocalDateTime createTime;

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

    public BookStoreUser getUser() {
        return user;
    }

    public void setUser(BookStoreUser user) {
        this.user = user;
    }

    public Byte getRate() {
        return rate;
    }

    public Integer getRateInt() {
        return rate.intValue();
    }

    public void setRate(Byte rate) {
        this.rate = rate;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
