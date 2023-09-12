package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.Book2User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
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

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Book2User> book2users = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthenticationType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthenticationType authType) {
        this.authType = authType;
    }

    public List<Book2User> getBook2users() {
        return book2users;
    }

    public void setBook2users(List<Book2User> book2users) {
        this.book2users = book2users;
    }
}
