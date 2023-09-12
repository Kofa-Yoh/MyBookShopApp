package com.example.MyBookShopApp.data;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book2user_type")
public class Book2UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code")
    private Book2UserTypeDto code;

    private String name;

    @OneToMany(mappedBy = "linkType")
    private List<Book2User> book2Users = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book2UserTypeDto getCode() {
        return code;
    }

    public void setCode(Book2UserTypeDto code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book2User> getBook2Users() {
        return book2Users;
    }

    public void setBook2Users(List<Book2User> book2Users) {
        this.book2Users = book2Users;
    }
}
