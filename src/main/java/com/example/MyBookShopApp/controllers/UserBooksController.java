package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.Book2UserService;
import com.example.MyBookShopApp.data.Book2UserTypeDto;
import com.example.MyBookShopApp.data.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class UserBooksController {

    private final BookRepository bookRepository;
    private final BookStoreUserRegister bookStoreUserRegister;
    private final Book2UserService book2UserService;

    public UserBooksController(BookRepository bookRepository, BookStoreUserRegister bookStoreUserRegister, Book2UserService book2UserService) {
        this.bookRepository = bookRepository;
        this.bookStoreUserRegister = bookStoreUserRegister;
        this.book2UserService = book2UserService;
    }

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    HttpServletResponse response,
                                    Model model) {

        List<Book> booksList = getUserBooksList("cart", cartContents);

        String booksInCookie = null;
        if (booksList.size() > 0) {
            booksInCookie = getNewCookieContent(booksList);
        } else {
            booksInCookie = "";
        }
        if (!cartContents.equals(booksInCookie)) {
            Cookie cookie = new Cookie("cartContents", booksInCookie);
            cookie.setPath("/books");
            response.addCookie(cookie);
        }

        model.addAttribute("booksList", booksList);
        model.addAttribute("fullPriceOld", booksList.stream()
                .mapToInt(book -> book.getPriceOld())
                .sum());
        model.addAttribute("fullPrice", booksList.stream()
                .mapToInt(book -> book.getPriceWithDiscount())
                .sum());
        model.addAttribute("isCartEmpty", booksList.size() == 0);

        return "cart";
    }

    @GetMapping("/postponed")
    public String postponedBooksPage(@CookieValue(value = "postponedContents", required = false) String postponedContents,
                                     HttpServletResponse response,
                                     Model model) {

        List<Book> booksList = getUserBooksList("postponed", postponedContents);

        String booksInCookie = null;
        if (booksList.size() > 0) {
            booksInCookie = getNewCookieContent(booksList);
        } else {
            booksInCookie = "";
        }
        if (!postponedContents.equals(booksInCookie)) {
            Cookie cookie = new Cookie("postponedContents", booksInCookie);
            cookie.setPath("/books");
            response.addCookie(cookie);
        }

        model.addAttribute("booksList", booksList);
        model.addAttribute("isPostponedEmpty", booksList.size() == 0);
        model.addAttribute("booksSlugList", booksList.stream()
                .map(book -> book.getSlug())
                .collect(Collectors.joining("\',\'", "\'", "\'")));

        return "postponed";
    }

    private String getNewCookieContent(List<Book> booksList) {
        return booksList.stream()
                .map(book -> book.getSlug())
                .collect(Collectors.joining("/"));
    }

    private List<Book> getUserBooksList(String page,
                                        String cookieContents) {

        List<Book> booksList = new ArrayList<>();

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();

        if (currentUser == null) {
            booksList.addAll(getUserBooksFromCookie(cookieContents));
        } else {
            if (page == "cart") {
                booksList.addAll(book2UserService.getBooksByUserAndLinkType(currentUser.getBookStoreUser(), Book2UserTypeDto.CART));
            } else if (page == "postponed") {
                booksList.addAll(book2UserService.getBooksByUserAndLinkType(currentUser.getBookStoreUser(), Book2UserTypeDto.KEPT));
            }
        }
        return booksList;
    }

    private List<Book> getUserBooksFromCookie(String cookieContents) {
        if (cookieContents == null || cookieContents.equals("")) {
            return new ArrayList<>();
        } else {
            cookieContents = cookieContents.startsWith("/") ? cookieContents.substring(1) : cookieContents;
            cookieContents = cookieContents.endsWith("/") ? cookieContents.substring(0, cookieContents.length() - 1) : cookieContents;
            ArrayList<String> cookieSlugs = new ArrayList<>(Arrays.asList(cookieContents.split("/")));
            return new ArrayList<>(bookRepository.findBooksBySlugIn(cookieSlugs));
        }
    }
}
