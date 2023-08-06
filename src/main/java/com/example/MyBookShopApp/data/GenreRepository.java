package com.example.MyBookShopApp.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GenreRepository extends CrudRepository<Genre, Integer> {

    List<Genre> findGenresByRootIsNullOrderById();

    Genre getGenreByName(String genreName);
}
