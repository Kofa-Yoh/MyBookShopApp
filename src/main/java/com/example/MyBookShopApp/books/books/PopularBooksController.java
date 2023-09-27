package com.example.MyBookShopApp.books.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PopularBooksController {

    private final BookService bookService;

    @Autowired
    public PopularBooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksList")
    public List<BookDto> booksList() {
        return new ArrayList<BookDto>();
    }

    @GetMapping("/books/popular")
    public String recentBooksPage(Model model) {
        model.addAttribute("booksList", bookService.getPageOfPopularBooks(0, 20).getContent());
        return "books/popular";
    }

    @GetMapping("/books/popular/page")
    @ResponseBody
    public BooksPageDto getNextRecentPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }
}
