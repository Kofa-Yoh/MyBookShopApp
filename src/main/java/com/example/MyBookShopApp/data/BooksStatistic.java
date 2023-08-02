package com.example.MyBookShopApp.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Date;

@Schema(description = "Books statistics")
@Entity
@Table(name = "books_statistic")
public class BooksStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @Column(name = "buyers_count")
    @Schema(description = "количество пользователей, купивших книгу")
    private Integer buyersCount;

    @Column(name = "in_cart_count")
    @Schema(description = "количество пользователей, у которых книга находится в корзине")
    private Integer inCartCount;

    @Column(name = "postponed_count")
    @Schema(description = "количество пользователей, у которых книга отложена")
    private Integer postponedCount;

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

    public Integer getBuyersCount() {
        return buyersCount;
    }

    public void setBuyersCount(Integer buyersCount) {
        this.buyersCount = buyersCount;
    }

    public Integer getInCartCount() {
        return inCartCount;
    }

    public void setInCartCount(Integer inCartCount) {
        this.inCartCount = inCartCount;
    }

    public Integer getPostponedCount() {
        return postponedCount;
    }

    public void setPostponedCount(Integer postponedCount) {
        this.postponedCount = postponedCount;
    }
}
