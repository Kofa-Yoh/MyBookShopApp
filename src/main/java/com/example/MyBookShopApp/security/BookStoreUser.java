package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.books.usersbooks.Book2User;
import com.example.MyBookShopApp.books.assessments.BookAssessment;
import com.example.MyBookShopApp.books.reviews.BookReview;
import com.example.MyBookShopApp.books.reviews.BookReviewAssessment;
import com.example.MyBookShopApp.user_transactions.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
@Getter
@Setter
public class BookStoreUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String phone;
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthenticationType authType;

    private Double balance;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID hash;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Book2User> book2users = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BookAssessment> bookRate = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BookReview> bookReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BookReviewAssessment> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
}
