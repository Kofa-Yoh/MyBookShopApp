package com.example.MyBookShopApp.commons.controllers;

import com.example.MyBookShopApp.books.search.SearchWordDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SearchCommonController {

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }
}
