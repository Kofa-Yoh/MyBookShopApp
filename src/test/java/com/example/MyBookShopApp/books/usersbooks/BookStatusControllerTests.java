package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookStatusControllerTests {

    private final MockMvc mockMvc;
    private final String bookSlug = "book-amh-155";
    private final String email = "testing@gmail.com";
    private Book book;
    private BookStoreUser user;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookStoreUserRepository bookStoreUserRepository;
    @Autowired
    private Book2UserRepository book2UserRepository;

    @Autowired
    BookStatusControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        book = bookRepository.findBookBySlug(bookSlug);
        user = bookStoreUserRepository.findBookStoreUserByEmail(email);
    }

    @AfterEach
    void tearDown() {
        book = null;
        user = null;
    }

    @Test
    void handleChangeBookStatusToCart() throws Exception {
        String newStatus = "CART";
        mockMvc.perform(post("/books/changeBookStatus/{slugs}/{status}",
                        bookSlug, newStatus))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("cartContents").getValue())
                                .contains(bookSlug))
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("postponedContents").getValue())
                                .doesNotContain(bookSlug));
    }

    @Test
    @WithUserDetails(email)
    void handleChangeBookStatusToKept() throws Exception {
        String newStatus = "KEPT";
        mockMvc.perform(post("/books/changeBookStatus/{slugs}/{status}",
                        bookSlug, newStatus))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("postponedContents").getValue())
                                .contains(bookSlug))
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("cartContents").getValue())
                                .doesNotContain(bookSlug));

        List<Book2User> book2UserKeptList = book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, Book2UserTypeDto.KEPT);
        assertFalse(book2UserKeptList.isEmpty());
        List<Book2User> book2UserCartList = book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, Book2UserTypeDto.CART);
        assertTrue(book2UserCartList.isEmpty());
    }

    @Test
    @WithUserDetails(email)
    void handleChangeBookStatusToUnlink() throws Exception {
        String newStatus = "UNLINK";
        mockMvc.perform(post("/books/changeBookStatus/{slugs}/{status}",
                        bookSlug, newStatus))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("cartContents").getValue())
                                .doesNotContain(bookSlug))
                .andExpect(result ->
                        assertThat(result.getResponse().getCookie("postponedContents").getValue())
                                .doesNotContain(bookSlug));

        List<Book2User> book2UserKeptList = book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, Book2UserTypeDto.KEPT);
        assertTrue(book2UserKeptList.isEmpty());
        List<Book2User> book2UserCartList = book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, Book2UserTypeDto.CART);
        assertTrue(book2UserCartList.isEmpty());
    }
}