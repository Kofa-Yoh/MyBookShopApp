package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookStatusController {

    private final BookRepository bookRepository;
    private final BookStoreUserRegister bookStoreUserRegister;
    private final Book2UserService book2UserService;

    private final BookAssessmentService bookAssessmentService;

    private final BookReviewService bookReviewService;

    private final BookReviewAssessmentRepository bookReviewAssessmentRepository;

    public BookStatusController(BookRepository bookRepository, BookStoreUserRegister bookStoreUserRegister, Book2UserService book2UserService, BookAssessmentService bookAssessmentService, BookReviewService bookReviewService, BookReviewAssessmentRepository bookReviewAssessmentRepository) {
        this.bookRepository = bookRepository;
        this.bookStoreUserRegister = bookStoreUserRegister;
        this.book2UserService = book2UserService;
        this.bookAssessmentService = bookAssessmentService;
        this.bookReviewService = bookReviewService;
        this.bookReviewAssessmentRepository = bookReviewAssessmentRepository;
    }

    @PostMapping("/changeBookStatus/{slugs}/{status}")
    @ResponseBody
    public ChangeBookStatusResponse handleChangeBookStatus(@PathVariable("slugs") String slugs,
                                                           @PathVariable("status") String status,
                                                           @CookieValue(name = "cartContents", required = false) String cartContents,
                                                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                           HttpServletResponse response,
                                                           Model model) {

        ChangeBookStatusResponse resultResponse = new ChangeBookStatusResponse();
        if (slugs == null || status == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Не все параметры заполнены");
            return resultResponse;
        }

        ArrayList<String> slugList = new ArrayList<>(Arrays.asList(
                slugs.replace("'", "")
                        .split(",")));

        String newCartContents = null;
        String newPostponedContents = null;

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser != null) {
            BookStoreUser bookStoreUser = currentUser.getBookStoreUser();
            LocalDateTime time = LocalDateTime.now();
            List<Book> books = bookRepository.findBooksBySlugIn(slugList);
            for (Book book : books) {
                switch (status) {
                    case ("UNLINK"):
                        book2UserService.removeBook2UserWithLinkType(book, bookStoreUser, Book2UserTypeDto.KEPT);
                        book2UserService.removeBook2UserWithLinkType(book, bookStoreUser, Book2UserTypeDto.CART);
                        break;
                    case ("CART"):
                        book2UserService.removeBook2UserWithLinkType(book, bookStoreUser, Book2UserTypeDto.KEPT);
                        book2UserService.addBook2User(book, bookStoreUser, status, time);
                        break;
                    case ("KEPT"):
                        book2UserService.removeBook2UserWithLinkType(book, bookStoreUser, Book2UserTypeDto.CART);
                        book2UserService.addBook2User(book, bookStoreUser, status, time);
                        break;
                }
            }
            newCartContents = getNewCookieContent(book2UserService.getBooksByUserAndLinkType(bookStoreUser, Book2UserTypeDto.CART));
            newPostponedContents = getNewCookieContent(book2UserService.getBooksByUserAndLinkType(bookStoreUser, Book2UserTypeDto.KEPT));
        } else {
            if (status.equals("CART")) {
                newCartContents = addInUserBooksCookies(slugList, cartContents);
                newPostponedContents = removeInUserBooksCookies(slugList, postponedContents);
            } else if (status.equals("KEPT")) {
                newCartContents = removeInUserBooksCookies(slugList, cartContents);
                newPostponedContents = addInUserBooksCookies(slugList, postponedContents);
            } else if (status.equals("UNLINK")) {
                newCartContents = removeInUserBooksCookies(slugList, cartContents);
                newPostponedContents = removeInUserBooksCookies(slugList, postponedContents);
            }
        }

        Cookie cartCookie = new Cookie("cartContents", newCartContents);
        cartCookie.setPath("/books");
        response.addCookie(cartCookie);

        Cookie postponedCookie = new Cookie("postponedContents", newPostponedContents);
        postponedCookie.setPath("/books");
        response.addCookie(postponedCookie);

        resultResponse.setResult(true);
        return resultResponse;
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
        if (payload == null || payload.getReviewid() == null || payload.getValue() == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            resultResponse.setResult(false);
            return resultResponse;
        }

        BookReview bookReview = bookReviewService.getBookReviewById(payload.getReviewid());
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

    private String addInUserBooksCookies(ArrayList<String> slugs, String cookieContents) {
        StringJoiner stringJoiner = new StringJoiner("/");
        stringJoiner.add(cookieContents);
        for (String slug : slugs) {
            if (!cookieContents.contains(slug)) {
                stringJoiner.add(slug);
            }
        }
        return stringJoiner.toString();
    }

    private String removeInUserBooksCookies(ArrayList<String> slugs, String cookieContents) {
        String result;
        if (cookieContents == null || cookieContents.equals("")) {
            result = "";
        } else {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cookieContents.split("/")));
            for (String slug : slugs) {
                cookieBooks.remove(slug);
            }
            if (cookieBooks.size() > 0) {
                result = String.join("/", cookieBooks);
            } else {
                result = "";
            }
        }
        return result;
    }

    private String getNewCookieContent(List<BookDto> booksList) {
        return booksList.stream()
                .map(book -> book.getSlug())
                .collect(Collectors.joining("/"));
    }
}
