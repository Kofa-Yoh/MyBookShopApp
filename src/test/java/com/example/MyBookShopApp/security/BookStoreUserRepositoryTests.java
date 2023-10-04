package com.example.MyBookShopApp.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookStoreUserRepositoryTests {

    private final BookStoreUserRepository bookStoreUserRepository;

    @Autowired
    BookStoreUserRepositoryTests(BookStoreUserRepository bookStoreUserRepository) {
        this.bookStoreUserRepository = bookStoreUserRepository;
    }

    @Test
    public void testAddNewUser() {
        BookStoreUser user = new BookStoreUser();
        user.setPassword("123456");
        user.setPhone("123456");
        user.setName("Tester");
        user.setEmail("testing@gmail.com");

        assertNotNull(bookStoreUserRepository.save(user));
    }
}