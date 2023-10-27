package com.example.MyBookShopApp.books.usersbooks;

import com.example.MyBookShopApp.books.books.BookDto;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    public String getPaymentUrl(List<BookDto> booksList) throws NoSuchAlgorithmException {
        Double paymentSumTotal = booksList
                .stream()
                .mapToDouble(BookDto::getDiscountPrice)
                .sum();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "123";
        md.update((merchantLogin + ":" + paymentSumTotal.toString() + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx?" +
                "MerchantLogin=" + merchantLogin +
                "&OutSum=" + paymentSumTotal.toString() +
                "&InvoiceID=" + invId +
                "&Description=Buy books" +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&Culture=ru" +
                "&IsTest=1";
    }
}
