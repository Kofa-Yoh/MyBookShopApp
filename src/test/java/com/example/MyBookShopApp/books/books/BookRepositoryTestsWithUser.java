package com.example.MyBookShopApp.books.books;

import com.example.MyBookShopApp.author.Author;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTestsWithUser {

    private final BookRepository bookRepository;
    private Integer user_id;

    @Autowired
    BookRepositoryTestsWithUser(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @BeforeEach
    void setUp() {
        user_id = 1;
    }

    @AfterEach
    void tearDown() {
        user_id = null;
    }

    @Test
    @Transactional
    void getBooksForUserOrderedByPubDateAndRating() {
        List<Book> recommendedBooks = bookRepository.getBooksListForUserOrderedByPubDateAndRating(user_id);
        assertNotNull(recommendedBooks);
        assertFalse(recommendedBooks.isEmpty());

        // Нет книг, которые уже есть у пользователя
        List<Book> userBooks = bookRepository.findBooksByBook2users_User_Id(user_id);
        assertTrue(recommendedBooks.stream()
                .filter(book -> userBooks.contains(book))
                .findAny()
                .isEmpty());

        List<Author> userAuthors = userBooks.stream()
                .map(Book::getBookAuthorsList)
                .flatMap(List::stream)
                .distinct()
                .toList();

        // Первая рекомендованная книга выбирается по автору, который есть у пользователя
        assertFalse(recommendedBooks.get(0).getBookAuthorsList()
                .stream()
                .filter(author -> userAuthors.contains(author))
                .findAny()
                .isEmpty());
    }
}