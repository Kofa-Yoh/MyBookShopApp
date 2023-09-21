package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2User, Integer> {

    List<Book2User> findBook2UsersByUserAndLinkType_Code(BookStoreUser bookStoreUser, Book2UserTypeDto linkType);

    List<Book2User> findBook2UsersByBookAndUserAndLinkType_Code(Book book, BookStoreUser bookStoreUser, Book2UserTypeDto linkType);
}
