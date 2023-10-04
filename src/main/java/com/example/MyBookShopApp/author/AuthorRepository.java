package com.example.MyBookShopApp.author;

import com.example.MyBookShopApp.books.books.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Integer> {

    List<Author> findAll();

    Author findAuthorBySlug(String authorSlug);

    List<Author> findAuthorsByBook2Authors_Book(Book book);
}
