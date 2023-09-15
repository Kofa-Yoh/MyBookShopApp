package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface BookReviewRepository extends CrudRepository<BookReview, Integer> {

    BookReview findBookReviewById(Integer id);

    List<BookReview> findBookReviewsByUserAndBook(BookStoreUser user, Book book);

    List<BookReview> findBookReviewsByBook(Book book);
}
