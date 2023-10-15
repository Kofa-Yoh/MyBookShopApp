package com.example.MyBookShopApp.security.oauth2;

import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final BookStoreOAuth2UserService userService;
    private final JWTUtil jwtUtil;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        BookStoreOAuth2User oAuth2User = (BookStoreOAuth2User) authentication.getPrincipal();
        // Записываем пользователя в БД
        BookStoreUserDetails userDetails = userService.saveUserInDb(oAuth2User);
        // Создаем JWT токен
        String jwtToken = jwtUtil.generateToken(userDetails);
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setPath("/");
        response.addCookie(cookie);

        new DefaultRedirectStrategy().sendRedirect(request, response, "/my");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
