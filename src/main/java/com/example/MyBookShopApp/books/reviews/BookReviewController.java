package com.example.MyBookShopApp.books.reviews;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.books.assessments.RateBookResponse;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/books")
@AllArgsConstructor
public class BookReviewController {

    private final BookRepository bookRepository;

    private final BookStoreUserRegister bookStoreUserRegister;

    private final BookReviewService bookReviewService;

    private final BookReviewAssessmentRepository bookReviewAssessmentRepository;

    @PostMapping("/bookReview")
    @ResponseBody
    public BookReviewResponse handleBookReview(@RequestBody BookReviewPayload bookReviewPayload) {
        BookReviewResponse resultResponse = new BookReviewResponse();
        if (bookReviewPayload == null || bookReviewPayload.getBookId() == null ||
                bookReviewPayload.getText() == null || bookReviewPayload.getText().equals("")) {
            resultResponse.setResult(false);
            resultResponse.setError("Отзыв слишком короткий. Напишите, пожалуйста, более развернутый отзыв");
            return resultResponse;
        }

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Пользователь не определен");
            return resultResponse;
        }

        Book book = bookRepository.findBookBySlug(bookReviewPayload.getBookId());
        if (book == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Книга не определена");
            return resultResponse;
        }

        bookReviewService.saveReview(book, currentUser.getBookStoreUser(), bookReviewPayload.getText());

        resultResponse.setResult(true);
        return resultResponse;
    }

    @PostMapping("/rateBookReview")
    @ResponseBody
    public RateBookResponse handleRateBookReview(@RequestBody BookReviewLikePayload payload) {
        RateBookResponse resultResponse = new RateBookResponse();
        if (payload == null || payload.getReviewId() == null || payload.getValue() == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookReview bookReview = bookReviewService.getBookReviewById(payload.getReviewId());
        if (bookReview == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookReviewAssessment assessment = bookReviewAssessmentRepository.findBookReviewAssessmentByReviewAndUser(
                bookReview, currentUser.getBookStoreUser());
        if (assessment == null) {
            BookReviewAssessment newAssessment = new BookReviewAssessment();
            newAssessment.setReview(bookReview);
            newAssessment.setUser(currentUser.getBookStoreUser());
            newAssessment.setAssessment(payload.getValue());
            newAssessment.setCreateTime(LocalDateTime.now());
            bookReviewAssessmentRepository.save(newAssessment);
        } else {
            if (assessment.getAssessment() != payload.getValue()) {
                assessment.setAssessment(payload.getValue());
                assessment.setCreateTime(LocalDateTime.now());
                bookReviewAssessmentRepository.save(assessment);
            }
        }

        resultResponse.setResult(true);
        return resultResponse;
    }
}
