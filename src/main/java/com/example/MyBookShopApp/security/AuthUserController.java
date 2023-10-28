package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.jwt.TokenBlacklistService;
import com.example.MyBookShopApp.security.verification.SmsCode;
import com.example.MyBookShopApp.security.verification.SmsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            } else {
                response.setResult("true");
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
        model.addAttribute("curUsr", userRegister.getCurrentUserDto());
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
