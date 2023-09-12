package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SearchCommonController {

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }
}
