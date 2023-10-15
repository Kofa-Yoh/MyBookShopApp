package com.example.MyBookShopApp.security;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class BookStoreUserRepositoryTests {

    private final BookStoreUserRepository bookStoreUserRepository;

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