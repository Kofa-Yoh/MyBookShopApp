package com.example.MyBookShopApp.books.popularity;

import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.commons.utils.MappingUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
