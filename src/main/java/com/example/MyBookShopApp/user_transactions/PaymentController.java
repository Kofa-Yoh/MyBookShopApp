package com.example.MyBookShopApp.user_transactions;

import com.example.MyBookShopApp.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookStoreUserRegister bookStoreUserRegister;

    @PostMapping("/topup")
    @ResponseBody
    public TransuctionResponse handlePayment(@RequestBody PaymentPayload paymentPayload) throws NoSuchAlgorithmException {
        TransuctionResponse result = new TransuctionResponse();
        result.setResult("false");
        BookStoreUser user;
        Double sum;
        LocalDateTime time;
        try {
            user = bookStoreUserRegister.findBookStoreUserByHash(paymentPayload.getHash());
            sum = Double.parseDouble(paymentPayload.getSum());
            time = Instant.ofEpochMilli(paymentPayload.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (IllegalArgumentException | NullPointerException e) {
            result.setError("Проверьте формат введенных данных");
            return result;
        }
        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (user == null || currentUser == null || user.getId() != currentUser.getBookStoreUser().getId()) {
            result.setError("Пользователь не определен. Обратитесь в тех. поддержку");
            return result;
        }
        if (sum <= 0) {
            result.setError("Сумма должна быть числом больше 0");
            return result;
        }
        Transaction transaction = new Transaction();
        transaction.setDescription("Пополнение баланса через Robokassa");
        Long orderId = paymentService.getNextPaymentId();
        transaction.setOrderId(orderId);
        transaction.setUser(user);
        transaction.setValue(sum);
        transaction.setTime(time);
        transaction.setStatus((byte) 0);
        paymentService.saveTransaction(transaction);
        result.setResult(paymentService.getPaymentUrl(sum, orderId, "Bookshop balance increase", user.getHash().toString(), "/topup"));
        return result;
    }

    @ModelAttribute("topupResult")
    public String topupResult() {
        return "";
    }

    @PostMapping("/payment/robokassa/{status}")
    public String handlePaymentResult(@PathVariable("status") String status,
                                      @RequestBody String postPayload,
                                      RedirectAttributes redirectAttributes) {
        String urlForTransactionFromProfile = "profile#transactions";
        String urlForTransactionFromCart = "books/cart";

        BookStoreUserDetails currentUser = bookStoreUserRegister.getCurrentUser();
        if (currentUser == null) {
            return urlForTransactionFromCart;
        }

        if (status.equals("fail")) {
            return "redirect:/" + urlForTransactionFromProfile;
        } else if (status.equals("success") && postPayload != null && postPayload.length() > 0) {
            Map<String, String> robokassaResponse = getPayloadMap(postPayload);
            Integer rows = 0;
            String invId = robokassaResponse.get("InvId");
            if (invId != null) {
                rows = paymentService.changeTransactionsStatus(Long.valueOf(invId), (byte) 1, postPayload);
            }
            if (rows == 0) {
                redirectAttributes.addFlashAttribute("topupResult", "Произошла ошибка обработки ответа от сервиса. Обратитесь в тех. поддержку");
            } else {
                redirectAttributes.addFlashAttribute("topupResult", "Оплата прошла успешно");
            }
            return "redirect:/" + urlForTransactionFromProfile;
        }
        return "redirect:/" + urlForTransactionFromProfile;
    }

    private Map<String, String> getPayloadMap(String postPayload) {
        return Arrays.stream(postPayload.split("&"))
                .map(elem -> elem.split("="))
                .collect(Collectors.toMap(elem -> elem[0], elem -> elem[1]));
    }
}
