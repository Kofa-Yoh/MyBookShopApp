package com.example.MyBookShopApp.books.genre;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {

    GenreRepository genreRepository;

    public List<Genre> getRootGenresList() {
        return genreRepository.findGenresByRootIsNullOrderById();
    }

    public Genre getGenreByName(String genreName) {
        return genreRepository.getGenreByName(genreName);
    }
}