package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class TagController {

    private final BookService bookService;
    private final TagService tagService;

    @Autowired
    public TagController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @ModelAttribute("tag")
    public String tag() {
        return "";
    }

    @ModelAttribute("tagsList")
    public Map<Tag, String> tagsList() {
        return tagService.getTagsMapWithClass();
    }

    @ModelAttribute("booksList")
    public List<Book> searchTagResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }


    @GetMapping({"tags", "tags/{tag}"})
    public String getSearchTagResults(@PathVariable(value = "tag", required = false) String tag,
                                      Model model) {
        model.addAttribute("tag", tag);
        model.addAttribute("tagsList", tagService.getTagsMapWithClass());
        model.addAttribute("booksList",
                bookService.getPageOfSearchTagResultBooks(tag, 0, 20).getContent());
        return "/tags/index";
    }

    @GetMapping("/tags/page/{tag}")
    @ResponseBody
    public BooksPageDto getNextTagPage(@RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer limit,
                                       @PathVariable(value = "tag", required = false) String tag) {
        return new BooksPageDto(bookService.getPageOfSearchTagResultBooks(tag, offset, limit).getContent());
    }
}