package com.example.MyBookShopApp.books.books;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class RecentBooksController {

    private final BookService bookService;
    private final Date DATE_FILTER_BEGIN = DateUtils.addMonths(new Date(), -3);
    private final Date DATE_FILTER_END = new Date();

    @Autowired
    public RecentBooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksList")
    public List<BookDto> booksList() {
        return bookService.getPageOfRecentBooksByPubDateBetween(DATE_FILTER_BEGIN, DATE_FILTER_END, 0, 20).getContent();
    }

    @ModelAttribute("dateFilterDto")
    public DateFilterDto dateFilterDto() {
        return new DateFilterDto(DATE_FILTER_BEGIN, DATE_FILTER_END);
    }

    @GetMapping("/books/recent")
    public String recentBooksPage(DateFilterDto dateFilterDto, Model model) {
        model.addAttribute("dateFilterDto", dateFilterDto);
        model.addAttribute("booksList", bookService.getPageOfRecentBooksByPubDateBetween(
                dateFilterDto.getBeginDate(), dateFilterDto.getEndDate(), 0, 20).getContent());
        return "books/recent";
    }

    @GetMapping("/books/recent/page")
    @ResponseBody
    public BooksPageDto getNextRecentPage(@RequestParam("from") String from,
                                          @RequestParam("to") String to,
                                          @RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit) {
        DateFilterDto dateFilterDto = new DateFilterDto();
        dateFilterDto.setBegin(from);
        dateFilterDto.setEnd(to);
        return new BooksPageDto(bookService.getPageOfRecentBooksByPubDateBetween(
                dateFilterDto.getBeginDate(), dateFilterDto.getEndDate(), offset, limit).getContent());
    }
}
