package com.example.MyBookShopApp.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksRatingAndPopularityService {

    private final BooksRatingAndPopularityRepository booksRatingAndPopularityRepository;
    private final Double IN_CART_RATE = 0.7;
    private final Double POSTPONED_RATE = 0.4;

    public BooksRatingAndPopularityService(BooksRatingAndPopularityRepository booksRatingAndPopularityRepository) {
        this.booksRatingAndPopularityRepository = booksRatingAndPopularityRepository;
    }

    public Double getBookPopularity(Integer book_id){
//        BooksStatistic booksStatistic = booksRatingAndPopularityRepository.findBooksStatisticByBook_Id(book_id);
//        if(booksStatistic == null) return 0.0;
//        return booksStatistic.getBuyersCount() +
//                IN_CART_RATE * booksStatistic.getInCartCount() +
//                POSTPONED_RATE * booksStatistic.getPostponedCount();
        return 0.0;
    }

    public Page<Book> getPopularBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return booksRatingAndPopularityRepository.getBooksOrderedByPopularity(nextPage);
    }

}
