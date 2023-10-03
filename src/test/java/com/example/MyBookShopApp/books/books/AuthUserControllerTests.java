package com.example.MyBookShopApp.books.books;

import com.example.MyBookShopApp.security.RegistrationForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthUserControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    AuthUserControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void mainPageAccessTest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(content().string(containsString("")))
                .andExpect(status().isOk());
    }

    @Test
    public void accessOnlyAuthorizedPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    public void correctLoginTest() throws Exception {
        mockMvc.perform(formLogin("/signin")
                        .user("testing@gmail.com")
                        .password("123"))
                .andDo(print())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails("testing@gmail.com")
    public void AuthenticaredAccessToProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/span[1]")
                        .string("Tester"));
    }

    @Test
    public void registrationTest() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setName("New User");
        registrationForm.setEmail("newuser@gmail.com");
        registrationForm.setPhone("+79111234567");
        registrationForm.setPass("123");

        mockMvc.perform(post("/reg")
                        .flashAttr("registrationForm", registrationForm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("regOk", true))
        ;
    }

    @Test
    public void handleLogout() throws Exception {
        mockMvc.perform(get("/user_logout"))
                .andDo(print())
                .andExpect(cookie().value("token", ""))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
        ;
    }
}