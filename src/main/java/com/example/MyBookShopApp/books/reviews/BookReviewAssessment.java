package com.example.MyBookShopApp.books.reviews;

import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_review_likes")
@Getter
@Setter
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
}
