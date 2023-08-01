package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.AuthorsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AuthorsController {

    private AuthorsService authorsService;

    @Autowired
    public AuthorsController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<Author>> authorsMap(){
        Map<String,List<Author>> authorsMap = authorsService.getAuthorsMap();
        return authorsMap;
    }

    @GetMapping("/authors")
    public String authorsPage(Model model){
        model.addAttribute("authorData", authorsService.getAuthorsMap());
        return "authors/index";
    }

    @Operation(summary = "Get books", description = "method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<Author>> authors(){
        return authorsService.getAuthorsMap();
    }
}
