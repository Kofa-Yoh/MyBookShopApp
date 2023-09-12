package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.MappingUtils;
import com.example.MyBookShopApp.data.UserDto;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BookStoreUserRegister {

    private final BookStoreUserRepository bookStoreUserRepository;
    private final BookStoreUserDetailService bookStoreUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Autowired
    public BookStoreUserRegister(BookStoreUserRepository bookStoreUserRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 BookStoreUserDetailService bookStoreUserDetailService, JWTUtil jwtUtil) {
        this.bookStoreUserRepository = bookStoreUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookStoreUserDetailService = bookStoreUserDetailService;
        this.jwtUtil = jwtUtil;
    }

    public void registerNewUser(RegistrationForm registrationForm) {
        BookStoreUser bookStoreUserByEmail = bookStoreUserRepository.findBookStoreUserByEmail(registrationForm.getEmail());
        if (bookStoreUserByEmail == null) {
            BookStoreUser user = new BookStoreUser();
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            user.setAuthType(AuthenticationType.DATABASE);
            bookStoreUserRepository.save(user);
        } else if (bookStoreUserByEmail.getAuthType() != AuthenticationType.DATABASE) {
            bookStoreUserByEmail.setName(registrationForm.getName());
            bookStoreUserByEmail.setPhone(registrationForm.getPhone());
            bookStoreUserByEmail.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            bookStoreUserByEmail.setAuthType(AuthenticationType.DATABASE);
            bookStoreUserRepository.save(bookStoreUserByEmail);
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        BookStoreUserDetails userDetails = (BookStoreUserDetails) bookStoreUserDetailService.loadUserByUsername(payload.getContact());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetails,
                payload.getCode(),
                userDetails.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookStoreUserDetails userDetails = (BookStoreUserDetails) bookStoreUserDetailService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public UserDto getCurrentUserDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            BookStoreUserDetails userDetails =
                    (BookStoreUserDetails) authentication.getPrincipal();
            UserDto userDto = MappingUtils.mapToUserDto(userDetails);
            return userDto;
        } else {
            UserDto userDto = new UserDto();
            return userDto;
        }
    }

    public BookStoreUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (BookStoreUserDetails) authentication.getPrincipal();
        } else {
            return null;
        }
    }
}
