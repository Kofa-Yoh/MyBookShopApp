package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookRepository bookRepository;
    private final ResourceStorage storage;
    private final AuthorsService authorsService;
    private final TagService tagService;
    private final BookAssessmentService bookAssessmentService;
    private final BookStoreUserRegister bookStoreUserRegister;
    private final BookReviewService bookReviewService;

    @Autowired
    public BooksController(BookRepository bookRepository, ResourceStorage storage, AuthorsService authorsService, TagService tagService, BookAssessmentService bookAssessmentService, BookStoreUserRegister bookStoreUserRegister, BookReviewService bookReviewService) {
        this.bookRepository = bookRepository;
        this.storage = storage;
        this.authorsService = authorsService;
        this.tagService = tagService;
        this.bookAssessmentService = bookAssessmentService;
        this.bookStoreUserRegister = bookStoreUserRegister;
        this.bookReviewService = bookReviewService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        Book book = bookRepository.findBookBySlug(slug);
        BookDto bookDto = MappingUtils.mapToBookDto(book);
        List<AuthorDto> authorDtos = authorsService.convertAuthorsListToDto(book.getBookAuthorsList());
        List<TagDto> tagDtos = tagService.convertTagListToDto(book.getTags().stream().toList());
        List<BookFile> bookFileList = book.getBookFileList();
        List<BookFileDto> fileDtos = new ArrayList<>();
        if (bookFileList != null) {
            fileDtos = bookFileList
                    .stream()
                    .map(MappingUtils::mapToBookFileDto)
                    .toList();
        }
        model.addAttribute("slugBook", bookDto);
        model.addAttribute("slugBookAuthors", authorDtos);
        model.addAttribute("slugBookTags", tagDtos);
        model.addAttribute("slugBookFiles", fileDtos);
        model.addAttribute("bookUsersRatesCount", bookAssessmentService.getBookUsersRatesCount(book));
        model.addAttribute("bookRateMap", bookAssessmentService.getBookUsersRates(book));
        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            model.addAttribute("bookUserRate", 0);
        } else {
            model.addAttribute("bookUserRate",
                    bookAssessmentService.getBookUserRate(currentUser.getBookStoreUser(), book));
        }
        model.addAttribute("bookReviews", bookReviewService.getReviewList(book));

        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file, slug);
        Book bookToUpdate = bookRepository.findBookBySlug(slug);
        bookToUpdate.setImage(savePath);
        bookRepository.save(bookToUpdate); //save new path to db

        return ("redirect:/books/" + slug);
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file path: " + path);

        MediaType mediaType = storage.getBookFileName(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}
