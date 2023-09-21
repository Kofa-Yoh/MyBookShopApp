package com.example.MyBookShopApp.books.reviews;

import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewAssessmentRepository extends JpaRepository<BookReviewAssessment, Integer> {

    BookReviewAssessment findBookReviewAssessmentByReviewAndUser(BookReview review, BookStoreUser user);
}
