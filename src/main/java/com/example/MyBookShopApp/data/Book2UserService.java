package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserRegister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class Book2UserService {

    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public Book2UserService(Book2UserRepository book2UserRepository, BookStoreUserRegister bookStoreUserRegister, Book2UserTypeRepository book2UserTypeRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    public List<BookDto> getBooksByUserAndLinkType(BookStoreUser user, Book2UserTypeDto linkType) {
        if (user != null) {
            return book2UserRepository.findBook2UsersByUserAndLinkType_Code(user, linkType)
                    .stream()
                    .map(Book2User::getBook)
                    .map(MappingUtils::mapToBookDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public void addBook2User(Book book, BookStoreUser user, String status, LocalDateTime time) {
        Book2UserTypeDto linkType;
        try {
            linkType = Book2UserTypeDto.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            Logger.getLogger(this.getClass().getSimpleName()).info("no link type : " + status);
            return;
        }
        if (book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, linkType).size() == 0) {
            Book2User book2User = new Book2User();
            book2User.setBook(book);
            book2User.setUser(user);
            book2User.setTime(time);
            book2User.setLinkType(book2UserTypeRepository.findByCode(linkType));
            book2UserRepository.save(book2User);
        }
    }

    public void removeBook2UserWithLinkType(Book book, BookStoreUser user, Book2UserTypeDto linkType) {
        List<Book2User> list = book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, linkType);
        book2UserRepository.findBook2UsersByBookAndUserAndLinkType_Code(book, user, linkType)
                .stream()
                .forEach(book2UserRepository::delete);
    }
}
