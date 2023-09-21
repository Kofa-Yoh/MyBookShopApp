package com.example.MyBookShopApp.books.assessments;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/books")
public class BookAssessmentController {

    private final BookRepository bookRepository;

    private final BookStoreUserRegister bookStoreUserRegister;

    private final BookAssessmentService bookAssessmentService;

    public BookAssessmentController(BookRepository bookRepository, BookStoreUserRegister bookStoreUserRegister, BookAssessmentService bookAssessmentService) {
        this.bookRepository = bookRepository;
        this.bookStoreUserRegister = bookStoreUserRegister;
        this.bookAssessmentService = bookAssessmentService;
    }

    @PostMapping("/rateBook")
    @ResponseBody
    public RateBookResponse handleRateBook(@RequestBody BookAssessmentPayload bookAssessmentPayload) {
        RateBookResponse resultResponse = new RateBookResponse();
        if (bookAssessmentPayload == null || bookAssessmentPayload.getBookId() == null || bookAssessmentPayload.getValue() == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        Book book = bookRepository.findBookBySlug(bookAssessmentPayload.getBookId());
        if (book == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        bookAssessmentService.changeBookUserAssessment(currentUser.getBookStoreUser(), book, Byte.parseByte(bookAssessmentPayload.getValue()));

        resultResponse.setResult(true);
        return resultResponse;
    }
}
