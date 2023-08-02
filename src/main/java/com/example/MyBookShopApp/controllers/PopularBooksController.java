package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
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

    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @Autowired
    public PopularBooksController(BooksRatingAndPopularityService booksRatingAndPopularityService) {
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
    }

    @ModelAttribute("booksList")
    public List<Book> booksList() {
        return new ArrayList<Book>();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/books/popular")
    public String recentBooksPage(Model model) {
        model.addAttribute("booksList", booksRatingAndPopularityService.getPopularBooks(0, 20).getContent());
        return "books/popular";
    }

    @GetMapping("/books/popular/page")
    @ResponseBody
    public BooksPageDto getNextRecentPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit) {
        return new BooksPageDto(booksRatingAndPopularityService.getPopularBooks(offset, limit).getContent());
    }
}
