package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.jwt.TokenBlacklistService;
import com.example.MyBookShopApp.security.verification.SmsCode;
import com.example.MyBookShopApp.security.verification.SmsService;
import com.example.MyBookShopApp.security.verification.UserChanges;
import com.example.MyBookShopApp.security.verification.UserChangesRepository;
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
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private final UserChangesRepository userChangesRepository;

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

        SmsCode smsCode = new SmsCode(smsService.generateCode(), 300); // 5 min
        smsService.saveNewCode(smsCode);
        try {
            sendMessageByEmail(payload.getContact(), "Bookstore email verification", "Verification code is: " + smsCode.getCode());
            response.setResult("true");
            return response;
        } catch (MailException e) {
            response.setResult("false");
            return response;
        }
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
    public List<TransactionDto> transactionList() {
        BookStoreUserDetails currentUser = userRegister.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        } else {
            return paymentService.getPageOfTransactionDtoList(currentUser.getBookStoreUser(), "desc", 0, 50).getContent();
        }
    }

    @GetMapping("/transactions")
    @ResponseBody
    public TransactionPageDto getTransactionsPage(@RequestParam("sort") String sort, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
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
                                      RedirectAttributes redirectAttributes) {
        String url = "redirect:/profile";
        ProfileSavingResults result = new ProfileSavingResults();
        result.setResult(false);
        Boolean isChanged = false;

        BookStoreUserDetails bookStoreUserDetails = userRegister.getCurrentUser();
        if (bookStoreUserDetails == null) {
            result.setErrors("Пользователь не определен. Выполните вход в личный кабинет еще раз.");
            redirectAttributes.addFlashAttribute("savingResult", result);
            return url;
        }

        BookStoreUser user = bookStoreUserDetails.getBookStoreUser();
        UserChanges changes = new UserChanges();

        if (payload.getPassword() != null && !payload.getPassword().equals("")) {
            String newPassword = payload.getPassword().trim();

            if (!newPassword.equals(payload.getPasswordReply().trim())) {
                result.setErrors("Введенные пароли должны совпадать.");
                redirectAttributes.addFlashAttribute("savingResult", result);
                return url;
            }

            changes.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            isChanged = true;
        }
        if (payload.getName() != null && !payload.getName().trim().equals("") && !payload.getName().equals(user.getName())) {
            changes.setName(payload.getName().trim());
            isChanged = true;
        }
        if (payload.getMail() != null && !payload.getMail().equals("") && !payload.getMail().equals(user.getEmail())) {
            changes.setEmail(payload.getMail());
            isChanged = true;
        }
        if (payload.getPhone() != null && !payload.getPhone().equals("") && !payload.getPhone().equals(user.getPhone())) {
            changes.setPhone(payload.getPhone());
            isChanged = true;
        }

        if (!isChanged) {
            result.setErrors("Данные не изменены.");
            redirectAttributes.addFlashAttribute("savingResult", result);
            return url;
        }

        changes.setUser(user);
        UserChanges userChanges = userChangesRepository.save(changes);
        String redirectLink = "http://localhost:8089/approveProfileSaving/c=" +
                userChanges.getId() + "&u=" + user.getHash();
        String textMessage = "Подтвердите изменения вашего профиля, перейдите по ссылке: " + redirectLink;
        try {
            sendMessageByEmail(user.getEmail(), "Saving profile verification", textMessage);
            result.setResult(true);
            redirectAttributes.addFlashAttribute("savingResult", result);
            return url;
        } catch (MailException e) {
            result.setErrors("Не удалось отправить подтверждение на email.");
            redirectAttributes.addFlashAttribute("savingResult", result);
            return url;
        }
    }

    @GetMapping("/approveProfileSaving/{payload}")
    public String handleApproveProfileSaving(@PathVariable("payload") String payload,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        String url = "redirect:/profile";
        Map<String, String> payloadMap = getPayloadMap(payload);
        if (payloadMap.get("c") == null || payloadMap.get("u") == null) {
            return url;
        }

        UserChanges changes = userChangesRepository.findUserChangesById(Long.valueOf(payloadMap.get("c")));
        if (changes == null) {
            return url;
        }
        BookStoreUser user = changes.getUser();
        if (changes.getName() != null) {
            user.setName(changes.getName());
        }
        if (changes.getEmail() != null) {
            user.setEmail(changes.getEmail());
        }
        if (changes.getPhone() != null) {
            user.setPhone(changes.getPhone());
        }
        if (changes.getPassword() != null) {
            user.setPassword(changes.getPassword());
        }

        userRegister.changeUser(user);
        String token = getTokenCookie(request);
        return clearSession(token, request, response);
    }

    @GetMapping("/user_logout")
    public String handleLogout(@CookieValue(value = "token", required = false) String tokenCookie,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        return clearSession(tokenCookie, request, response);
    }

    private String clearSession(String tokenCookie, HttpServletRequest request, HttpServletResponse response) {
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

    private Map<String, String> getPayloadMap(String payload) {
        if (payload.length() == 0) {
            return new HashMap<>();
        }
        return Arrays.stream(payload.split("&"))
                .map(elem -> elem.split("="))
                .collect(Collectors.toMap(elem -> elem[0], elem -> elem[1]));
    }

    private String getTokenCookie(HttpServletRequest request) {
        String token = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }
        return token;
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

    private void sendMessageByEmail(String sendTo, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(sendTo);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}
