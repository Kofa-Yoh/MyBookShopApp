package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookRepository;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/books")
@AllArgsConstructor
public class BookStatusController {

    private final BookRepository bookRepository;

    private final BookStoreUserRegister bookStoreUserRegister;

    private final Book2UserService book2UserService;

    @PostMapping("/changeBookStatus/{slugs}/{status}")
    @ResponseBody
    public ChangeBookStatusResponse handleChangeBookStatus(@PathVariable("slugs") String slugs,
                                                           @PathVariable("status") String status,
                                                           @CookieValue(name = "cartContents", required = false) String cartContents,
                                                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                           HttpServletResponse response) {
        ChangeBookStatusResponse resultResponse = new ChangeBookStatusResponse();
        if (slugs == null || status == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Не все параметры заполнены");
            return resultResponse;
        }

        List<String> slugList = new ArrayList<>(getSlugList(slugs));

        CookiesContents cookiesContents;
        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser != null) {
            List<Book> books = bookRepository.findBooksBySlugIn(slugList);
            cookiesContents = book2UserService.changeBook2UserStatus(books, currentUser.getBookStoreUser(), status);
        } else {
            cookiesContents = book2UserService.changeBookStatusInCookies(slugList, status, new CookiesContents(cartContents, postponedContents));
        }

        book2UserService.changeCookiesContents(cookiesContents, response);

        resultResponse.setResult(true);
        return resultResponse;
    }

    private List<String> getSlugList(String slugs) {
        return Arrays.asList(
                slugs.replace("'", "")
                        .split(","));
    }
}
