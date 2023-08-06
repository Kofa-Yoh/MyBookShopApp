package com.example.MyBookShopApp.data;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getRootGenresList() {
        return genreRepository.findGenresByRootIsNullOrderById();
    }

    public Genre getGenreByName(String genreName) {
        return genreRepository.getGenreByName(genreName);
    }
}