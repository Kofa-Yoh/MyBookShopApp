package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.jwt.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

@Controller
public class AuthUserController {

    private final BookStoreUserRegister userRegister;
    private final BookStoreUserDetailService bookStoreUserDetailService;
    private final JWTUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthUserController(BookStoreUserRegister userRegister, BookStoreUserDetailService bookStoreUserDetailService, JWTUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.userRegister = userRegister;
        this.bookStoreUserDetailService = bookStoreUserDetailService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/signin")
    public String handleSignIn() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) {
        userRegister.registerNewUser(registrationForm);
        model.addAttribute("regOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) {
//        ContactConfirmationResponse response = userRegister.login(payload);
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "profile";
    }

    @GetMapping("/user_logout")
    public String handleLogout(@CookieValue(value = "token", required = false) String tokenCookie,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        addTokenInBlacklist(tokenCookie);

        Cookie cookie = new Cookie("token", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return "redirect:/signin";
    }

    private void addTokenInBlacklist(String tokenCookie) {
        if (tokenCookie != null) {
            try {
                String username = jwtUtil.extractUsername(tokenCookie);
                BookStoreUserDetails userDetails =
                        (BookStoreUserDetails) bookStoreUserDetailService.loadUserByUsername(username);
                if (jwtUtil.validateToken(tokenCookie, userDetails)) {
                    LocalDateTime expirationDate = jwtUtil.extractExpiration(tokenCookie)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    Long seconds = Duration.between(LocalDateTime.now(), expirationDate).getSeconds();
                    tokenBlacklistService.addTokenInBlacklist(tokenCookie,
                            LocalDateTime.now(),
                            seconds);
                }
            } catch (ExpiredJwtException e) {
                Logger.getLogger(this.getClass().getSimpleName()).warning(e.getMessage());
            }
        }
    }
}
