package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_review_likes")
public class BookReviewAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private BookReview review;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookStoreUser user;

    @ColumnDefault("0")
    private Byte assessment;

    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookReview getReview() {
        return review;
    }

    public void setReview(BookReview review) {
        this.review = review;
    }

    public BookStoreUser getUser() {
        return user;
    }

    public void setUser(BookStoreUser user) {
        this.user = user;
    }

    @ColumnDefault("0")
    public Byte getAssessment() {
        return assessment;
    }

    public void setAssessment(Byte assessment) {
        this.assessment = assessment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
