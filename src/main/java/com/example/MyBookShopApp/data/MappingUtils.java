package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUserDetails;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class MappingUtils {

    private static BookAssessmentService bookAssessmentService;

    public MappingUtils(BookAssessmentService bookAssessmentService) {
        this.bookAssessmentService = bookAssessmentService;
    }

    public static UserDto mapToUserDto(BookStoreUserDetails entity){
        UserDto dto = new UserDto();
        dto.setName(entity.getBookStoreUser().getName());
        dto.setEmail(entity.getBookStoreUser().getEmail());
        dto.setPhone(entity.getBookStoreUser().getPhone());
        dto.setRoles(entity.getAuthorities().stream().map(a->UserRoleType.getRoleType(a.toString())).collect(Collectors.toSet()));
        return dto;
    }
    public static BookDto mapToBookDto(Book book){
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
}
