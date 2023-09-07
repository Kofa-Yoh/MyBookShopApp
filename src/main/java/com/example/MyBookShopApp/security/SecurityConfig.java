package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.MyBookShopApp.security.oauth2.OAuthLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BookStoreUserDetailService bookStoreUserDetailService;

    private final JWTUtil jwtUtil;

    @Autowired
    public SecurityConfig(BookStoreUserDetailService bookStoreUserDetailService, JWTUtil jwtUtil) {
        this.bookStoreUserDetailService = bookStoreUserDetailService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTRequestFilter filter() {
        return new JWTRequestFilter(bookStoreUserDetailService, jwtUtil);
    }

    @Bean
    public AccessDeniedExceptionFilter accessFilter() {
        return  new AccessDeniedExceptionFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(bookStoreUserDetailService);
        authProvider.setPasswordEncoder(getPasswordEncoder());
        return authProvider;
    }

    @Autowired
    private OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/my", "/profile").authenticated()
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/signin")
                        .failureUrl("/signin")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/my")
                        .successHandler(oAuthLoginSuccessHandler)
                        .failureHandler(authenticationFailureHandler()))
                .oauth2Client(withDefaults())
                .logout(form -> form
                        .logoutSuccessUrl("/signin")
                        .deleteCookies("token"));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterAfter(filter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(accessFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
