package com.example.MyBookShopApp.books.reviews;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_review")
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookStoreUser user;

    private String review;

    private LocalDateTime createTime;

    @OneToMany(mappedBy = "review")
    @JsonIgnore
    private List<BookReviewAssessment> reviewLikes = new ArrayList<>();

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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<BookReviewAssessment> getReviewLikes() {
        return reviewLikes;
    }

    public void setReviewLikes(List<BookReviewAssessment> reviewLikes) {
        this.reviewLikes = reviewLikes;
    }
}
