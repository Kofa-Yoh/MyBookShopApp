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

    public ProfileSavingResults changeUser(ProfileForm payload) {
        ProfileSavingResults result = new ProfileSavingResults();
        result.setResult(false);
        Boolean isChanged = false;
        BookStoreUserDetails bookStoreUserDetails = getCurrentUser();
        BookStoreUser user;
        if (bookStoreUserDetails == null) {
            result.setErrors("Current user is not defined");
            return result;
        } else {
            user = bookStoreUserDetails.getBookStoreUser();
        }
        if (payload.getPassword() != null && !payload.getPassword().equals("")) {
            String newPassword = payload.getPassword().trim();
            if (!newPassword.equals(payload.getPasswordReply().trim())) {
                result.setErrors("Entered passwords are not equal");
                return result;
            } else {
                user.setPassword(newPassword);
                isChanged = true;
            }
        }
        if (payload.getName() != null && !payload.getName().trim().equals("") && !payload.getName().equals(user.getName())) {
            user.setName(payload.getName().trim());
            isChanged = true;
        }
        if (payload.getMail() != null && !payload.getMail().equals("") && !payload.getMail().equals(user.getEmail())) {
            user.setEmail(payload.getMail());
            isChanged = true;
        }
        if (payload.getPhone() != null && !payload.getPhone().equals("") && !payload.getPhone().equals(user.getPhone())) {
            user.setPhone(payload.getPhone());
            isChanged = true;
        }

        if (isChanged) {
            user.setAuthType(AuthenticationType.DATABASE);
            BookStoreUser savedUser = bookStoreUserRepository.save(user);
            result.setResult(true);
            result.setUser(savedUser);
            return result;
        } else {
            result.setErrors("Data wasn't changed");
            result.setUser(user);
            return result;
        }
    }
}
