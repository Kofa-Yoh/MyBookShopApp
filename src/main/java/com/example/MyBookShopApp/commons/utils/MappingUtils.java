package com.example.MyBookShopApp.commons.utils;

import com.example.MyBookShopApp.author.Book2Author;
import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.assessments.BookAssessmentService;
import com.example.MyBookShopApp.books.bookfiles.BookFile;
import com.example.MyBookShopApp.books.bookfiles.BookFileDto;
import com.example.MyBookShopApp.books.bookfiles.BookFileType;
import com.example.MyBookShopApp.books.reviews.*;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import com.example.MyBookShopApp.security.UserDto;
import com.example.MyBookShopApp.security.UserRoleType;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class MappingUtils {

    private static BookAssessmentService bookAssessmentService;
    private static BookReviewAssessmentRepository bookReviewAssessmentRepository;
    private static BookReviewService bookReviewService;
    private static BookStoreUserRegister bookStoreUserRegister;

    public MappingUtils(BookAssessmentService bookAssessmentService, BookReviewAssessmentRepository bookReviewAssessmentRepository, BookReviewService bookReviewService, BookStoreUserRegister bookStoreUserRegister) {
        this.bookAssessmentService = bookAssessmentService;
        this.bookReviewAssessmentRepository = bookReviewAssessmentRepository;
        this.bookReviewService = bookReviewService;
        this.bookStoreUserRegister = bookStoreUserRegister;
    }

    public static UserDto mapToUserDto(BookStoreUserDetails entity) {
        UserDto dto = new UserDto();
        dto.setName(entity.getBookStoreUser().getName());
        dto.setEmail(entity.getBookStoreUser().getEmail());
        dto.setPhone(entity.getBookStoreUser().getPhone());
        dto.setRoles(entity.getAuthorities().stream().map(a -> UserRoleType.getRoleType(a.toString())).collect(Collectors.toSet()));
        return dto;
    }

    public static BookDto mapToBookDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setSlug(book.getSlug());
        dto.setPubDate(book.getPubDate());
        dto.setIsBestseller(book.getIsBestseller());
        dto.setTitle(book.getTitle());
        dto.setImage(book.getImage());
        dto.setDescription(book.getDescription());

        String authors = book.getBook2Authors()
                .stream()
                .sorted(Comparator.comparing(Book2Author::getSortIndex))
                .map(book2Author -> book2Author.getAuthor().getName())
                .collect(Collectors.joining(", "));
        dto.setAuthors(authors);

        dto.setDiscount(book.getDiscountPercent());
        dto.setPrice(book.getPriceOld());
        dto.setDiscountPrice(book.getPriceWithDiscount());

        dto.setRating(bookAssessmentService.getBookRate(book));

        return dto;
    }

    public static BookFileDto mapToBookFileDto(BookFile file) {
        BookFileDto dto = new BookFileDto();
        dto.setId(file.getId());
        dto.setHash(file.getHash());
        dto.setPath(file.getPath());
        dto.setExtension(BookFileType.getFileExtensionStringByTypeId(file.getTypeId()));
        return dto;
    }

    public static BookReviewDto mapToBookReviewDto(BookReview review) {
        BookReviewDto dto = new BookReviewDto();
        dto.setId(review.getId());
        dto.setUserName(review.getUser().getName());
        dto.setReview(review.getReview());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dto.setCreateTime(review.getCreateTime().format(formatter));

        dto.setLikesCount(bookReviewService.getReviewLikes(review));
        dto.setDislikesCount(bookReviewService.getReviewDislikes(review));
        dto.setRating(bookReviewService.getReviewRating(review));

        Byte like = 0;
        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser != null) {
            BookReviewAssessment userAssessment = bookReviewAssessmentRepository.findBookReviewAssessmentByReviewAndUser(review, currentUser.getBookStoreUser());
            like = userAssessment == null ? 0 : userAssessment.getAssessment();
        }
        dto.setUserLike(like == null ? 0 : like);

        return dto;
    }
}
