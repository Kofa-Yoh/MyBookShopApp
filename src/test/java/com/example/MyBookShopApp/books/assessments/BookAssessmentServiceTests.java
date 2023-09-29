package com.example.MyBookShopApp.books.assessments;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookAssessmentServiceTests {

    private final BookRepository bookRepository;
    private final BookAssessmentService bookAssessmentService;

    @Autowired
    BookAssessmentServiceTests(BookRepository bookRepository, BookAssessmentService bookAssessmentService) {
        this.bookRepository = bookRepository;
        this.bookAssessmentService = bookAssessmentService;
    }

    @Test
    void getBookRate() {
        Book book = bookRepository.findBookById(1);
        Integer rate = bookAssessmentService.getBookRate(book);
        assertNotNull(rate);
        assertEquals(rate, 3);
    }

    @Test
    void getBookRateFail() {
        Book book = bookRepository.findBookById(2);
        Integer rate = bookAssessmentService.getBookRate(book);
        assertNotNull(rate);
        assertEquals(rate, 0);
    }
}