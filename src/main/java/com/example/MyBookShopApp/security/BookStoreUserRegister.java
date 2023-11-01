package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.commons.utils.MappingUtils;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BookStoreUserRegister {

    private final BookStoreUserRepository bookStoreUserRepository;
    private final BookStoreUserDetailService bookStoreUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public BookStoreUser registerNewUser(RegistrationForm registrationForm) {
        BookStoreUser bookStoreUserByEmail = bookStoreUserRepository.findBookStoreUserByEmail(registrationForm.getEmail());
        BookStoreUser bookStoreUserByPhone = bookStoreUserRepository.findBookStoreUserByPhone(registrationForm.getPhone());

        if (bookStoreUserByEmail == null && bookStoreUserByPhone == null) {
            BookStoreUser user = new BookStoreUser();
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            user.setAuthType(AuthenticationType.DATABASE);
            bookStoreUserRepository.save(user);
            return user;
        } else if (bookStoreUserByEmail != null && bookStoreUserByEmail.getAuthType() != AuthenticationType.DATABASE) {
            bookStoreUserByEmail.setName(registrationForm.getName());
            bookStoreUserByEmail.setPhone(registrationForm.getPhone());
            bookStoreUserByEmail.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            bookStoreUserByEmail.setAuthType(AuthenticationType.DATABASE);
            bookStoreUserRepository.save(bookStoreUserByEmail);
            return bookStoreUserByEmail;
        } else if (bookStoreUserByPhone != null && bookStoreUserByPhone.getAuthType() != AuthenticationType.DATABASE) {
            bookStoreUserByPhone.setName(registrationForm.getName());
            bookStoreUserByPhone.setPhone(registrationForm.getPhone());
            bookStoreUserByPhone.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            bookStoreUserByPhone.setAuthType(AuthenticationType.DATABASE);
            bookStoreUserRepository.save(bookStoreUserByPhone);
            return bookStoreUserByPhone;
        }
        return bookStoreUserByEmail == null ? bookStoreUserByPhone : bookStoreUserByEmail;
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
        BookStoreUserDetails userDetails = (BookStoreUserDetails) bookStoreUserDetailService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload) {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone(payload.getContact());
        registrationForm.setPass(payload.getCode());
        this.registerNewUser(registrationForm);
        UserDetails userDetails = bookStoreUserDetailService.loadUserByUsername(payload.getContact());
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

    public BookStoreUser changeUser(BookStoreUser user) {
        user.setAuthType(AuthenticationType.DATABASE);
        return bookStoreUserRepository.save(user);
    }

    public BookStoreUser findBookStoreUserByHash(String hash) {
        return bookStoreUserRepository.findBookStoreUserByHash(UUID.fromString(hash));
    }
}
