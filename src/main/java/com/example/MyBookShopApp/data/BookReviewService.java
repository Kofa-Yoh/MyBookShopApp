package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookReviewService {

    private final BookReviewRepository bookReviewRepository;

    public BookReviewService(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    public BookReview getBookReviewById(Integer id) {
        return bookReviewRepository.findBookReviewById(id);
    }

    public void saveReview(Book book, BookStoreUser currentUser, String text) {
        BookReview bookReview = new BookReview();
        bookReview.setBook(book);
        bookReview.setUser(currentUser);
        bookReview.setReview(text);
        bookReview.setCreateTime(LocalDateTime.now());
        bookReviewRepository.save(bookReview);
    }


    public List<BookReviewDto> getReviewList(Book book) {
        return bookReviewRepository.findBookReviewsByBook(book)
                .stream()
                .map(MappingUtils::mapToBookReviewDto)
                .sorted(BookReviewDto::compareByRatingSortedAlgorithm)
                .toList();
    }
}
