package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingUtils {

    private static BookAssessmentService bookAssessmentService;
    private static BookReviewAssessmentRepository bookReviewAssessmentRepository;
    private static BookStoreUserRegister bookStoreUserRegister;

    public MappingUtils(BookAssessmentService bookAssessmentService, BookReviewAssessmentRepository bookReviewAssessmentRepository, BookStoreUserRegister bookStoreUserRegister) {
        this.bookAssessmentService = bookAssessmentService;
        this.bookReviewAssessmentRepository = bookReviewAssessmentRepository;
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

        int likesCount;
        int dislikesCount;
        int rating;
        List<BookReviewAssessment> reviewLikes = review.getReviewLikes();
        if (reviewLikes.size() == 0) {
            likesCount = 0;
            dislikesCount = 0;
            rating = 0;
        } else {
            likesCount = (int) reviewLikes.stream()
                    .filter(assessment -> assessment.getAssessment() == 1)
                    .count();
            dislikesCount = (int) reviewLikes.stream()
                    .filter(assessment -> assessment.getAssessment() == -1)
                    .count();
            double ratingDouble = likesCount * 5 / (likesCount + dislikesCount);
            rating = Math.toIntExact(Math.round(ratingDouble > 0 && ratingDouble < 1 ? 1 : ratingDouble));
        }
        dto.setLikesCount(likesCount);
        dto.setDislikesCount(dislikesCount);
        dto.setRating(rating);
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
