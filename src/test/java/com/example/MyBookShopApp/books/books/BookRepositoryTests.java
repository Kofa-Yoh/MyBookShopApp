package com.example.MyBookShopApp.books.books;

import com.example.MyBookShopApp.author.Author;
import com.example.MyBookShopApp.author.AuthorRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class BookRepositoryTests {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Test
    void findBooksByBook2Authors_Author_Name() {
        String token = "Jesselyn Shepheard";

        List<Book> bookList = bookRepository.findBooksByBook2Authors_Author_Name(token);

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());

        for (Book book : bookList) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.toString());
            List<Author> bookAuthorsList = authorRepository.findAuthorsByBook2Authors_Book(book);
            assertThat(bookAuthorsList).map(author -> author.getName().equals(token));
        }
    }

    @Test
    void findBooksByTitleContaining() {
        String token = "Dream";
        List<Book> bookList = bookRepository.findBooksByTitleContaining(token);

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());

        for (Book book : bookList) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.toString());
            assertThat(book.getTitle()).contains(token);
        }
    }

    @Test
    void getBestsellers() {
        List<Book> bookList = bookRepository.getBestsellers();

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());
        assertThat(bookList.size()).isGreaterThan(1);
    }
}