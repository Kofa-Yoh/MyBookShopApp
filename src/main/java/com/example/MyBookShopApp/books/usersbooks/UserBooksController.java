package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import com.example.MyBookShopApp.commons.utils.MappingUtils;
import com.example.MyBookShopApp.user_transactions.PaymentService;
import com.example.MyBookShopApp.user_transactions.TransuctionResponse;
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
    public RedirectView handlePay(HttpServletRequest request,
                                  HttpServletResponse response) {
        String cartUrl = "/books/cart";

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser != null) {
            List<Book> booksList = getUserBooksList("cart", "");
            if (booksList.size() == 0) {
                return new RedirectView(cartUrl);
            }
            BookStoreUser user = currentUser.getBookStoreUser();
            TransuctionResponse result = paymentService.buyingBooksByUser(user, booksList);
            CookiesContents cookiesContents = book2UserService.changeBook2UserStatus(booksList, user, "PAID");
            book2UserService.changeCookiesContents(cookiesContents, response);
            return new RedirectView(cartUrl);
        } else {
            Map<String, String> cookies = getCookiesList(request.getCookies());

            String cartContents = cookies.get("cartContents");
            if (cartContents == null || cartContents.equals("")) {
                return new RedirectView(cartUrl);
            }

            List<Book> booksList = getUserBooksList("cart", cartContents);
            if (booksList.size() == 0) {
                return new RedirectView(cartUrl);
            }
            String paymentUrl;
            try {
                paymentUrl = paymentService.buyingBooksByAnonymousUser(booksList);
            } catch (NoSuchAlgorithmException e) {
                return new RedirectView(cartUrl);
            }
            String newCartContents = book2UserService.removeInUserBooksCookies(getBooksSlugsList(booksList), cartContents);
            book2UserService.changeCookiesContents(new CookiesContents(newCartContents, cookies.get("postponed")), response);
            return new RedirectView(paymentUrl);
        }
    }

    private Map<String, String> getCookiesList(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .collect(Collectors.toMap(c -> c.getName(), c -> c.getValue()));
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
