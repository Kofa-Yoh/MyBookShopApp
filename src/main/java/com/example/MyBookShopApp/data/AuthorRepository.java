package com.example.MyBookShopApp.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Integer> {

    List<Author> findAll();

    Author findAuthorBySlug(String authorSlug);
}
