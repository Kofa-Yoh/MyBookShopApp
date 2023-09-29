package com.example.MyBookShopApp.books.reviews;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookReviewServiceTests {

    private final BookReviewService bookReviewService;
    private BookReview bookReview;

    @Autowired
    BookReviewServiceTests(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @BeforeEach
    void setUp() {
        bookReview = bookReviewService.getBookReviewById(1);
    }

    @AfterEach
    void tearDown() {
        bookReview = null;
    }

    @Test
    @Transactional
    void reviewLikesCount() {
        Integer likes = bookReviewService.getReviewLikes(bookReview);
        assertNotNull(likes);
        assertEquals(bookReview.getReviewLikes()
                .stream()
                .filter(l -> l.getAssessment() == 1)
                .count(), (long) likes);
    }

    @Test
    @Transactional
    void reviewDisikesCount() {
        Integer dislikes = bookReviewService.getReviewDislikes(bookReview);
        assertNotNull(dislikes);
        assertEquals(bookReview.getReviewLikes()
                .stream()
                .filter(l -> l.getAssessment() == -1)
                .count(), (long) dislikes);
    }

    @Test
    @Transactional
    void reviewRating() {
        Integer rating = bookReviewService.getReviewRating(bookReview);
        assertNotNull(rating);
        assertEquals(rating, 5);
    }
}