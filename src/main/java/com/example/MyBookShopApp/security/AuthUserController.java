package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.jwt.TokenBlacklistService;
import com.example.MyBookShopApp.security.verification.SmsCode;
import com.example.MyBookShopApp.security.verification.SmsService;
import com.example.MyBookShopApp.user_transactions.PaymentService;
import com.example.MyBookShopApp.user_transactions.TransactionDto;
import com.example.MyBookShopApp.user_transactions.TransactionPageDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
@AllArgsConstructor
public class AuthUserController {

    @Value("${appEmail.email}")
    private static String email;

    private final BookStoreUserRegister userRegister;
    private final BookStoreUserDetailService bookStoreUserDetailService;
    private final JWTUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final SmsService smsService;
    private final JavaMailSender javaMailSender;
    private final PaymentService paymentService;

    @GetMapping("/signin")
    public String handleSignIn() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/checkContact")
    @ResponseBody
    public ContactConfirmationResponse handleCheckContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        try {
            UserDetails userDetails = bookStoreUserDetailService.loadUserByUsername(payload.getContact());
            if (userDetails == null) {
                response.setResult("false");
                return response;
            } else {
                BookStoreUserDetails currentUser = userRegister.getCurrentUser();
                if (currentUser != null) {
                    if (userDetails == currentUser) {
                        response.setResult("false");
                        return response;
                    } else {
                        response.setResult("true");
                        return response;
                    }
                } else {
                    response.setResult("true");
                    return response;
                }
            }
        } catch (Exception e) {
            response.setResult("false");
        }
        return response;
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String smsCodeString = smsService.sendSecretCodeSms(payload.getContact());
        smsService.saveNewCode(new SmsCode(smsCodeString, 60)); // expires in 1 min
        response.setResult("true");
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(payload.getContact());
        SmsCode smsCode = new SmsCode(smsService.generateCode(), 300); // 5 min
        smsService.saveNewCode(smsCode);
        message.setSubject("Bookstore email verification");
        message.setText("Verification code is: " + smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        if (smsService.verifyCode(payload.getCode())) {
            response.setResult("true");
        }
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
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                                HttpServletResponse httpServletResponse) {
        if (smsService.verifyCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userRegister.jwtLoginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            return loginResponse;
        } else {
            return null;
        }
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        model.addAttribute("curUsr", userRegister.getCurrentUserDto());
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        return "profile";
    }

    @ModelAttribute("transactionHistory")
    public List<TransactionDto> transactionList(){
        BookStoreUserDetails currentUser = userRegister.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        } else {
            return paymentService.getPageOfTransactionDtoList(currentUser.getBookStoreUser(), "desc", 0, 50).getContent();
        }
    }

    @GetMapping("/transactions")
    @ResponseBody
    public TransactionPageDto getTransactionsPage(@RequestParam("sort") String sort, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit){
        BookStoreUserDetails currentUser = userRegister.getCurrentUser();
        if (currentUser == null) {
            return new TransactionPageDto(new ArrayList<>());
        } else {
            return new TransactionPageDto(paymentService.getPageOfTransactionDtoList(currentUser.getBookStoreUser(), sort, offset, limit).getContent());
        }
    }

    @ModelAttribute("savingResult")
    public ProfileSavingResults savingResult() {
        ProfileSavingResults results = new ProfileSavingResults();
        results.setResult(false);
        return results;
    }

    @PostMapping("/saveProfile")
    public String handleProfileSaving(ProfileForm payload,
                                      HttpServletResponse httpServletResponse,
                                      RedirectAttributes redirectAttributes) {
        ProfileSavingResults results = userRegister.changeUser(payload);
        if (results != null && results.getUser() != null) {
            ContactConfirmationResponse token = new ContactConfirmationResponse();
            if (results.getUser().getEmail() != null && !results.getUser().getEmail().equals("")) {
                token = userRegister.jwtLogin(
                        new ContactConfirmationPayload(results.getUser().getEmail(), ""));
            } else if (results.getUser().getPhone() != null && !results.getUser().getPhone().equals("")) {
                token = userRegister.jwtLoginByPhoneNumber(
                        new ContactConfirmationPayload(results.getUser().getPhone(), ""));
            }
            Cookie cookie = new Cookie("token", token.getResult());
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }
        redirectAttributes.addFlashAttribute("savingResult", results);
        return "redirect:/profile";
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
