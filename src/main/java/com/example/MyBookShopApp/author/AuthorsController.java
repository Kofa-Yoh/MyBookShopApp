package com.example.MyBookShopApp.author;

import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.books.BookService;
import com.example.MyBookShopApp.books.books.BooksPageDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class AuthorsController {

    private AuthorsService authorsService;
    private BookService bookService;

    @ModelAttribute("authorsMap")
    public Map<String, List<Author>> authorsMap(){
        return new HashMap<>();
    }

    @ModelAttribute("authorSlug")
    public String authorSlug(){
        return "";
    }

    @ModelAttribute("authorInfo")
    public Author authorInfo(){
        return new Author();
    }

    @ModelAttribute("booksList")
    public List<BookDto> booksList(){
        return new ArrayList<>();
    }

    @ModelAttribute("booksCount")
    public int booksCount(){
        return 0;
    }

    @GetMapping("/authors")
    public String authorsPage(Model model){
        model.addAttribute("authorsMap", authorsService.getAuthorsMap());
        return "authors/index";
    }

    @GetMapping({ "authors/{authorSlug}"})
    public String getAuthorPage(@PathVariable(value = "authorSlug") String authorSlug,
                                      Model model) {
        model.addAttribute("authorSlug", authorSlug);
        model.addAttribute("authorInfo", authorsService.getAuthor(authorSlug));
        model.addAttribute("booksList",
                bookService.getPageOfBooksByAuthorSlug(authorSlug, 0, 6).getContent());
        model.addAttribute("booksCount", bookService.getBooksByAuthorSlug(authorSlug).size());
        return "/authors/slug";
    }

    @GetMapping({ "books/author/{authorSlug}"})
    public String getAuthorBooksPage(@PathVariable(value = "authorSlug") String authorSlug,
                                      Model model) {
        model.addAttribute("authorSlug", authorSlug);
        model.addAttribute("authorInfo", authorsService.getAuthor(authorSlug));
        model.addAttribute("booksList",
                bookService.getPageOfBooksByAuthorSlug(authorSlug, 0, 20).getContent());
        return "/books/author";
    }

    @GetMapping("/books/author/{authorSlug}/page")
    @ResponseBody
    public BooksPageDto getNextAuthorBooksPage(@RequestParam("offset") Integer offset,
                                               @RequestParam("limit") Integer limit,
                                               @PathVariable(value = "authorSlug", required = false) String authorSlug) {
        return new BooksPageDto(bookService.getPageOfBooksByAuthorSlug(authorSlug, offset, limit).getContent());
    }

    @Operation(summary = "Get books", description = "method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<AuthorDto>> authors(){
        return authorsService.getAuthorsMap();
    }
}
