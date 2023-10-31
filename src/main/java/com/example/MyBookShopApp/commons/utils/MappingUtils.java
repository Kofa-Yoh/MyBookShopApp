package com.example.MyBookShopApp.commons.utils;

import com.example.MyBookShopApp.author.Book2Author;
import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.books.books.BookDto;
import com.example.MyBookShopApp.books.assessments.BookAssessmentService;
import com.example.MyBookShopApp.books.bookfiles.BookFile;
import com.example.MyBookShopApp.books.bookfiles.BookFileDto;
import com.example.MyBookShopApp.books.bookfiles.BookFileType;
import com.example.MyBookShopApp.books.reviews.*;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.user_transactions.Transaction;
import com.example.MyBookShopApp.user_transactions.TransactionDto;
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

    private static final String DATE_TIME_FORMAT = "dd LLLL yyyy HH:mm";

    public MappingUtils(BookAssessmentService bookAssessmentService, BookReviewAssessmentRepository bookReviewAssessmentRepository, BookReviewService bookReviewService, BookStoreUserRegister bookStoreUserRegister) {
        this.bookAssessmentService = bookAssessmentService;
        this.bookReviewAssessmentRepository = bookReviewAssessmentRepository;
        this.bookReviewService = bookReviewService;
        this.bookStoreUserRegister = bookStoreUserRegister;
    }

    public static UserDto mapToUserDto(BookStoreUserDetails entity) {
        UserDto dto = new UserDto();
        BookStoreUser bookStoreUser = entity.getBookStoreUser();
        dto.setName(bookStoreUser.getName());
        dto.setEmail(bookStoreUser.getEmail());
        dto.setPhone(bookStoreUser.getPhone());
        dto.setHash(bookStoreUser.getHash().toString());
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

    public static TransactionDto mapToTransactionDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setTime(transaction.getTime().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        dto.setSum((transaction.getValue() > 0 ? "+" : "") + transaction.getValue() + " р.");
        dto.setDescription(transaction.getDescription());
        dto.setOrderId(transaction.getOrderId());
        dto.setStatus(transaction.getStatus());
        dto.setBook(transaction.getBook());
        return dto;
    }
}
