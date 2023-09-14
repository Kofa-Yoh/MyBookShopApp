package com.example.MyBookShopApp.data;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorsService {

    private final AuthorRepository authorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public AuthorsService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<AuthorDto>> getAuthorsMap() {
        return authorRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.groupingBy(
                        author -> author.getName().substring(0, 1)
                ));
    }

    public AuthorDto getAuthor(String slug) {
        return modelMapper.map(authorRepository.findAuthorBySlug(slug), AuthorDto.class);
    }

    public AuthorDto convertToDto(Author author) {
        AuthorDto authorDto = modelMapper.map(author, AuthorDto.class);
        return authorDto;
    }

    public List<AuthorDto> convertAuthorsListToDto(List<Author> authors) {
        return authors.stream()
                .map(this::convertToDto)
                .toList();
    }
}
