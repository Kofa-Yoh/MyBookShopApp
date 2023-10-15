package com.example.MyBookShopApp.books.usersbooks;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book2user_type")
@Getter
@Setter
public class Book2UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code")
    private Book2UserTypeDto code;

    private String name;

    @OneToMany(mappedBy = "linkType")
    private List<Book2User> book2Users = new ArrayList<>();
}
