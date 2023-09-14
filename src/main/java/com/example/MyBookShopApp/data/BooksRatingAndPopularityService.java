package com.example.MyBookShopApp.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksRatingAndPopularityService {

    private final BooksRatingAndPopularityRepository booksRatingAndPopularityRepository;

    public BooksRatingAndPopularityService(BooksRatingAndPopularityRepository booksRatingAndPopularityRepository) {
        this.booksRatingAndPopularityRepository = booksRatingAndPopularityRepository;
    }

    public Page<BookDto> getPopularBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return booksRatingAndPopularityRepository.getBooksOrderedByPopularity(nextPage)
                .map(MappingUtils::mapToBookDto);
    }

}
