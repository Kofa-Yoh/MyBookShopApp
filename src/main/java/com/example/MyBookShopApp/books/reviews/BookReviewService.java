package com.example.MyBookShopApp.books.reviews;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.commons.utils.MappingUtils;
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

    public Integer getReviewLikes(BookReview review) {
        List<BookReviewAssessment> reviewLikes = review.getReviewLikes();
        if (reviewLikes.size() == 0) {
            return 0;
        } else {
            return (int) reviewLikes.stream()
                    .filter(assessment -> assessment.getAssessment() == 1)
                    .count();
        }
    }

    public Integer getReviewDislikes(BookReview review) {
        List<BookReviewAssessment> reviewLikes = review.getReviewLikes();
        if (reviewLikes.size() == 0) {
            return 0;
        } else {
            return (int) reviewLikes.stream()
                    .filter(assessment -> assessment.getAssessment() == -1)
                    .count();
        }
    }

    public Integer getReviewRating(BookReview review) {
        int likesCount = getReviewLikes(review);
        int dislikesCount = getReviewDislikes(review);
        if (likesCount == 0 && dislikesCount == 0) {
            return 0;
        } else {
            double ratingDouble = likesCount * 5 / (likesCount + dislikesCount);
            return Math.toIntExact(Math.round(ratingDouble > 0 && ratingDouble < 1 ? 1 : ratingDouble));
        }
    }
}
