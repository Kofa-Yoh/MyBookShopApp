package com.example.MyBookShopApp.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorsService {

    private AuthorRepository authorRepository;

    public AuthorsService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        return authorRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                                author -> author.getName().substring(0, 1)
                        ));
    }

    public Author getAuthor(String slug){
        return authorRepository.findAuthorBySlug(slug);
    }
}
