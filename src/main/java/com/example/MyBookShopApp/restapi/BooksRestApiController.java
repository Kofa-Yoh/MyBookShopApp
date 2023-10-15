package com.example.MyBookShopApp.restapi;

import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.books.BookService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BooksRestApiController {

    private final BookService bookService;

    @GetMapping("/books/by-author")
    @Operation(summary = "Get book list by author's name", description = "operation to get book list by part of author's name")
    public ResponseEntity<List<BookDto>> booksByAuthor(@Parameter(description = "Enter the author's name or it's part", example = "Anselma")
                                                        @RequestParam("author") String authorName){
        return ResponseEntity.ok(bookService.getBooksByAuthorName(authorName));
    }

    @GetMapping("/books/by-title")
    @Operation(summary = "Get book list by title", description = "operation to get book list by part of title")
    public ResponseEntity<ApiResponse<BookDto>> booksByTitle(
            @Parameter(description = "Enter the book's title or it's part", example = "Tourist")
            @RequestParam("title") String title) throws BookstoreApiWrongParameterException {
        ApiResponse<BookDto> response = new ApiResponse<>();
        List<BookDto> data = bookService.getBooksByTitle(title);
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + data.size() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/by-price-range")
    @Operation(summary = "Get book list by price range")
    public ResponseEntity<List<BookDto>> priceRangeBooks(@RequestParam("min") Integer min, @RequestParam("max") Integer max){
        return ResponseEntity.ok(bookService.getBooksByPriceBetween(min, max));
    }

    @GetMapping("/books/by-price")
    @Operation(summary = "Get book list by price")
    public ResponseEntity<List<BookDto>> booksByPrice(@RequestParam("price") Integer price){
        return ResponseEntity.ok(bookService.getBooksWithPrice(price));
    }

    @GetMapping("/books/with-max-discount")
    @Operation(summary = "Get book list with max discount")
    public ResponseEntity<List<BookDto>> maxDiscountBooks(){
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount());
    }

    @GetMapping("/books/bestsellers")
    @Operation(summary = "Get top of the book list")
    public ResponseEntity<List<BookDto>> bestsellerBooks(){
        return ResponseEntity.ok(bookService.getBestsellers());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleMissingServletRequestParameterException(Exception exception){
        return new ResponseEntity<>(new ApiResponse<BookDto>(HttpStatus.BAD_REQUEST, "Missing required parameters",
                exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleBookstoreApiWrongParameterException(Exception exception){
        return new ResponseEntity<>(new ApiResponse<BookDto>(HttpStatus.BAD_REQUEST, "Bad parameter value...",
                exception), HttpStatus.BAD_REQUEST);
    }
}
