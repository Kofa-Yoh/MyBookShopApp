package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import com.example.MyBookShopApp.commons.utils.MappingUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
@AllArgsConstructor
public class UserBooksController {

    private final BookRepository bookRepository;
    private final BookStoreUserRegister bookStoreUserRegister;
    private final Book2UserService book2UserService;
    private final PaymentService paymentService;

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    HttpServletResponse response,
                                    Model model) {

        List<Book> booksList = getUserBooksList("cart", cartContents);

        String booksInCookie;
        if (booksList.size() > 0) {
            booksInCookie = getNewCookieContent(booksList);
        } else {
            booksInCookie = "";
        }
        if (cartContents == null || !cartContents.equals(booksInCookie)) {
            Cookie cookie = new Cookie("cartContents", booksInCookie);
            cookie.setPath("/books");
            response.addCookie(cookie);
        }

        List<BookDto> bookDtosList = getBookDtos(booksList);
        model.addAttribute("booksList", bookDtosList);
        model.addAttribute("fullPriceOld", getPriceOldSum(booksList));
        model.addAttribute("fullPrice", getPriceSum(booksList));
        model.addAttribute("isCartEmpty", bookDtosList.size() == 0);
        return "cart";
    }

    @GetMapping("/postponed")
    public String postponedBooksPage(@CookieValue(value = "postponedContents", required = false) String postponedContents,
                                     HttpServletResponse response,
                                     Model model) {

        List<Book> booksList = getUserBooksList("postponed", postponedContents);

        String booksInCookie;
        if (booksList.size() > 0) {
            booksInCookie = getNewCookieContent(booksList);
        } else {
            booksInCookie = "";
        }
        if (postponedContents == null || !postponedContents.equals(booksInCookie)) {
            Cookie cookie = new Cookie("postponedContents", booksInCookie);
            cookie.setPath("/books");
            response.addCookie(cookie);
        }

        List<BookDto> bookDtosList = getBookDtos(booksList);
        model.addAttribute("booksList", bookDtosList);
        model.addAttribute("isPostponedEmpty", bookDtosList.size() == 0);
        model.addAttribute("booksSlugList", getBooksSlugs(booksList));
        return "postponed";
    }

    @GetMapping("/pay")
    public RedirectView handlePay(@CookieValue(value = "cartContents", required = false) String cartContents) throws NoSuchAlgorithmException {
        List<BookDto> booksList = getUserBooksList("cart", cartContents);
        String paymentUrl = paymentService.getPaymentUrl(booksList);
        return new RedirectView(paymentUrl);
    }

    private String getNewCookieContent(List<BookDto> booksList) {
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
            return booksList;
        }

        if (page == "cart") {
            booksList.addAll(book2UserService.getBooksByUserAndLinkType(currentUser.getBookStoreUser(), Book2UserTypeDto.CART));
        } else if (page == "postponed") {
            booksList.addAll(book2UserService.getBooksByUserAndLinkType(currentUser.getBookStoreUser(), Book2UserTypeDto.KEPT));
        }
        return booksList;
    }

    private List<Book> getUserBooksFromCookie(String cookieContents) {
        if (cookieContents == null || cookieContents.equals("")) {
            return new ArrayList<>();
        }

        cookieContents = cookieContents.startsWith("/") ? cookieContents.substring(1) : cookieContents;
        cookieContents = cookieContents.endsWith("/") ? cookieContents.substring(0, cookieContents.length() - 1) : cookieContents;
        ArrayList<String> cookieSlugs = new ArrayList<>(Arrays.asList(cookieContents.split("/")));
        return bookRepository.findBooksBySlugIn(cookieSlugs);
    }

    private List<BookDto> getBookDtos(List<Book> booksList) {
        return booksList.stream().map(MappingUtils::mapToBookDto).toList();
    }

    private int getPriceSum(List<Book> booksList) {
        return booksList.stream()
                .mapToInt(book -> book.getPriceWithDiscount())
                .sum();
    }

    private int getPriceOldSum(List<Book> booksList) {
        return booksList.stream()
                .mapToInt(book -> book.getPriceOld())
                .sum();
    }

    private String getBooksSlugs(List<Book> booksList) {
        return booksList.stream()
                .map(book -> book.getSlug())
                .collect(Collectors.joining("\',\'", "\'", "\'"));
    }

    private List<String> getBooksSlugsList(List<Book> booksList) {
        return booksList.stream()
                .map(book -> book.getSlug())
                .toList();
    }
}
