package com.example.MyBookShopApp.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookStoreUserRegisterTestsAuth {

    @Value("${auth.secret}")
    private String secret;

    private final BookStoreUserRepository bookStoreUserRepository;
    private final BookStoreUserRegister userRegister;
    private ContactConfirmationPayload rightPayload;
    private ContactConfirmationPayload wrongPayload;

    @Autowired
    BookStoreUserRegisterTestsAuth(BookStoreUserRepository bookStoreUserRepository, BookStoreUserRegister userRegister) {
        this.bookStoreUserRepository = bookStoreUserRepository;
        this.userRegister = userRegister;
    }

    @BeforeEach
    void setUp() {
        rightPayload = new ContactConfirmationPayload();
        rightPayload.setContact("testing@gmail.com");
        rightPayload.setCode("123");

        wrongPayload = new ContactConfirmationPayload();
        wrongPayload.setContact("testing@gmail.com");
        wrongPayload.setCode("567");
    }

    @AfterEach
    void tearDown() {
        rightPayload = null;
        wrongPayload = null;
    }

    @Test
    void jwtLogin() {
        ContactConfirmationResponse response = userRegister.jwtLogin(rightPayload);

        assertNotNull(response);
        assertFalse(response.getResult().isEmpty());

        String usernameFromToken = Jwts.parser().setSigningKey(secret).parseClaimsJws(response.getResult()).getBody().getSubject();
        assertTrue(usernameFromToken.equals(rightPayload.getContact()));
    }

    @Test
    void jwtLoginFail() {
        ContactConfirmationResponse response = null;
        try {
            response = userRegister.jwtLogin(wrongPayload);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(e.getMessage());
        }
        assertNull(response);
    }
}