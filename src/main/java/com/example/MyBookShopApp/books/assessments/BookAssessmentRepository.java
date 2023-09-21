package com.example.MyBookShopApp.books.assessments;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookAssessmentRepository extends CrudRepository<BookAssessment, Integer> {

    BookAssessment findBookAssessmentByUserAndBook(BookStoreUser user, Book book);

    List<BookAssessment> findBookAssessmentsByBook(Book book);
}
