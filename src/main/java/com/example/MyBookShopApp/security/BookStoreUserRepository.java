package com.example.MyBookShopApp.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookStoreUserRepository extends JpaRepository<BookStoreUser, Integer> {

    BookStoreUser findBookStoreUserByEmail(String email);

    BookStoreUser findBookStoreUserByPhone(String phone);

    BookStoreUser findBookStoreUserByHash(UUID hash);
}
