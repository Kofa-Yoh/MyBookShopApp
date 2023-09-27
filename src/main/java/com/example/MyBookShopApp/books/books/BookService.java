package com.example.MyBookShopApp.books.books;

import com.example.MyBookShopApp.commons.utils.MappingUtils;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> getBooksData() {
        return bookRepository.findAll()
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }

    public Page<BookDto> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAll(nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfRecentBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAllByOrderByPubDateDesc(nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfRecentBooksByPubDateBetween(Date begin, Date end, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findByPubDateBetween(begin, end, nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getBooksOrderedByPopularity(nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfBooksByAuthorSlug(String authorSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByBook2Authors_Author_Slug(authorSlug, nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookByTitleContaining(searchWord, nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfSearchTagResultBooks(String tag, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookByTags_Name(tag, nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public Page<BookDto> getPageOfSearchGenreResultBooks(String genre, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookByGenres_Name(genre, nextPage)
                .map(MappingUtils::mapToBookDto);
    }

    public List<BookDto> getBooksByAuthorName(String authorName) {
        return bookRepository.findBooksByBook2Authors_Author_NameContaining(authorName)
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }

    public List<BookDto> getBooksByAuthorSlug(String authorSlug) {
        return bookRepository.findBooksByBook2Authors_Author_Slug(authorSlug)
                .stream()
                .map(MappingUtils::mapToBookDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.equals("") || title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<BookDto> data = bookRepository.findBooksByTitleContaining(title)
                    .stream()
                    .map(MappingUtils::mapToBookDto)
                    .toList();
            if (data.size() > 0) {
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public List<BookDto> getBooksByPriceBetween(Integer min, Integer max) {
        return bookRepository.findBooksByPriceOldBetween(min, max)
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }

    public List<BookDto> getBooksWithPrice(Integer price) {
        return bookRepository.findBooksByPriceOldIs(price)
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }

    public List<BookDto> getBestsellers() {
        return bookRepository.getBestsellers()
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }

    public List<BookDto> getBooksWithMaxDiscount() {
        return bookRepository.getBooksWithMaxDiscount()
                .stream()
                .map(MappingUtils::mapToBookDto)
                .toList();
    }
}
