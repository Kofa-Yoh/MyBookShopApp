package com.example.MyBookShopApp.books.genre;

import com.example.MyBookShopApp.books.books.BookService;
import com.example.MyBookShopApp.books.books.BooksPageDto;
import com.example.MyBookShopApp.books.search.SearchWordDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GenresController {

    private final GenreService genreService;

    private final BookService bookService;

    public GenresController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @ModelAttribute("genresList")
    public List<Genre> genresList() {
        return new ArrayList<>();
    }

    @ModelAttribute("genre")
    public String genre() {
        return "";
    }

    @ModelAttribute("genreSearch")
    public Genre genreSearch() {
        return null;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/genres")
    public String genresPage(Model model) {
        model.addAttribute("genresList", genreService.getRootGenresList());
        return "genres/index";
    }

    @GetMapping({"/genres/{genre}"})
    public String getSearchGenreResults(@PathVariable(value = "genre", required = false) String genre,
                                        Model model) {
        model.addAttribute("genre", genre);
        model.addAttribute("genreSearch", genreService.getGenreByName(genre));
        model.addAttribute("booksList",
                bookService.getPageOfSearchGenreResultBooks(genre, 0, 20).getContent());
        return "/genres/slug";
    }

    @GetMapping("/genres/page/{genre}")
    @ResponseBody
    public BooksPageDto getNextGenrePage(@RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer limit,
                                         @PathVariable(value = "genre", required = false) String genre) {
        return new BooksPageDto(bookService.getPageOfSearchGenreResultBooks(genre, offset, limit).getContent());
    }
}
