package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.BookStoreUserDetailService;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@AllArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookStoreUserDetailService bookStoreUserDetailService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    try {
                        username = jwtUtil.extractUsername(token);
                    } catch (ExpiredJwtException e) {
                        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getMessage());
                        logout(request, response);
                    }
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    BookStoreUserDetails userDetails =
                            (BookStoreUserDetails) bookStoreUserDetailService.loadUserByUsername(username);
                    try {
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    } catch (ExpiredJwtException e) {
                        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getMessage());
                        logout(request, response);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("token", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        new DefaultRedirectStrategy().sendRedirect(request, response, "/signin");
    }
}
