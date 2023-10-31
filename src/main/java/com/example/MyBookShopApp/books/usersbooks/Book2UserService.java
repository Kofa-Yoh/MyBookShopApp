package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Book2UserService {

    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public List<Book> getBooksByUserAndLinkType(BookStoreUser user, Book2UserTypeDto linkType) {
        if (user != null) {
            return book2UserRepository.findBook2UsersByUserAndLinkType_Code(user, linkType)
                    .stream()
                    .map(Book2User::getBook)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public void addBook2User(Book book, BookStoreUser user, Book2UserTypeDto linkType, LocalDateTime time) {
        if (book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, linkType).size() == 0) {
            Book2User book2User = new Book2User();
            book2User.setBook(book);
            book2User.setUser(user);
            book2User.setTime(time);
            book2User.setLinkType(book2UserTypeRepository.findByCode(linkType));
            book2UserRepository.save(book2User);
        }
    }

    public void removeBook2UserWithLinkType(Book book, BookStoreUser user, Book2UserTypeDto linkType) {
        book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, linkType)
                .stream()
                .forEach(book2UserRepository::delete);
    }

    public CookiesContents changeBook2UserStatus(List<Book> books, BookStoreUser user, String status) {
        LocalDateTime time = LocalDateTime.now();
        for (Book book : books) {
            switch (status) {
                case ("UNLINK"):
                    removeBook2UserWithLinkType(book, user, Book2UserTypeDto.KEPT);
                    removeBook2UserWithLinkType(book, user, Book2UserTypeDto.CART);
                    break;
                case ("CART"):
                    removeBook2UserWithLinkType(book, user, Book2UserTypeDto.KEPT);
                    addBook2User(book, user, Book2UserTypeDto.valueOf(status), time);
                    break;
                case ("KEPT"):
                case ("PAID"):
                    removeBook2UserWithLinkType(book, user, Book2UserTypeDto.CART);
                    addBook2User(book, user, Book2UserTypeDto.valueOf(status), time);
                    break;
            }
        }
        CookiesContents cookiesContents = new CookiesContents();
        cookiesContents.setCartContents(getNewCookieContent(getBooksByUserAndLinkType(user, Book2UserTypeDto.CART)));
        cookiesContents.setPostponedContents(getNewCookieContent(getBooksByUserAndLinkType(user, Book2UserTypeDto.KEPT)));
        return cookiesContents;
    }

    public CookiesContents changeBookStatusInCookies(List<String> slugList, String status, CookiesContents cookiesContents) {
        String newCartContents = "";
        String newPostponedContents = "";
        if (status.equals("CART")) {
            newCartContents = addInUserBooksCookies(slugList, cookiesContents.getCartContents());
            newPostponedContents = removeInUserBooksCookies(slugList, cookiesContents.getPostponedContents());
        } else if (status.equals("KEPT")) {
            newCartContents = removeInUserBooksCookies(slugList, cookiesContents.getCartContents());
            newPostponedContents = addInUserBooksCookies(slugList, cookiesContents.getPostponedContents());
        } else if (status.equals("UNLINK")) {
            newCartContents = removeInUserBooksCookies(slugList, cookiesContents.getCartContents());
            newPostponedContents = removeInUserBooksCookies(slugList, cookiesContents.getPostponedContents());
        }
        return new CookiesContents(newCartContents, newPostponedContents);
    }

    public void changeCookiesContents(CookiesContents cookiesContents, HttpServletResponse response) {
        Cookie cartCookie = new Cookie("cartContents", cookiesContents.getCartContents());
        cartCookie.setPath("/books");
        response.addCookie(cartCookie);

        Cookie postponedCookie = new Cookie("postponedContents", cookiesContents.getPostponedContents());
        postponedCookie.setPath("/books");
        response.addCookie(postponedCookie);
    }

    public String addInUserBooksCookies(List<String> slugs, String cookieContents) {
        cookieContents = cookieContents == null ? "" : cookieContents;
        StringJoiner stringJoiner = new StringJoiner("/");
        stringJoiner.add(cookieContents);
        for (String slug : slugs) {
            if (!cookieContents.contains(slug)) {
                stringJoiner.add(slug);
            }
        }
        return stringJoiner.toString();
    }

    public String removeInUserBooksCookies(List<String> slugs, String cookieContents) {
        String result;
        if (cookieContents == null || cookieContents.equals("")) {
            result = "";
            return result;
        }

        ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cookieContents.split("/")));
        for (String slug : slugs) {
            cookieBooks.remove(slug);
        }
        if (cookieBooks.size() > 0) {
            result = String.join("/", cookieBooks);
        } else {
            result = "";
        }
        return result;
    }

    public String getNewCookieContent(List<Book> booksList) {
        return booksList.stream()
                .map(book -> book.getSlug())
                .collect(Collectors.joining("/"));
    }
}
