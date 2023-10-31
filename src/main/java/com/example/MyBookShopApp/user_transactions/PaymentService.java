package com.example.MyBookShopApp.user_transactions;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.commons.utils.MappingUtils;
import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.EntityManager;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    private final EntityManager entityManager;
    private final TransactionRepository transactionRepository;

    public TransuctionResponse buyingBooksByUser(BookStoreUser user, List<Book> booksList) {
        TransuctionResponse result = new TransuctionResponse();
        Double paymentSumTotal = getTotalSum(booksList);
        Double balance = getBalance(user);

        if (paymentSumTotal > balance) {
            result.setResult("false");
            result.setError("На счете не хватает денег. Пополните баланс.");
            return result;
        }

        List<Transaction> transactions = new ArrayList<>();
        Long orderId = getNextPaymentId();
        String description = "Покупка книги";
        for (Book book : booksList) {
            Transaction transaction = new Transaction();
            transaction.setOrderId(orderId);
            transaction.setBook(book);
            transaction.setValue(-Double.valueOf(book.getPriceWithDiscount()));
            transaction.setUser(user);
            transaction.setTime(LocalDateTime.now());
            transaction.setStatus((byte) 1);
            transaction.setDescription(description);
            transactions.add(transaction);
        }
        transactionRepository.saveAll(transactions);
        result.setResult("Покупка книг на сумму " + paymentSumTotal + " р. прошла успешно.");
        return result;
    }

    public String buyingBooksByAnonymousUser(List<Book> booksList) throws NoSuchAlgorithmException {
        Double paymentSumTotal = getTotalSum(booksList);
        Long orderId = getNextPaymentId();
        String description = "Buying the book";
        String userHash = "0";
        String url = "/books/pay";
        return getPaymentUrl(paymentSumTotal, orderId, description, userHash, url);
    }

    public String getPaymentUrl(Double sum, Long orderId, String decription, String userHash, String url) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = orderId.toString();
        md.update((merchantLogin + ":" + sum.toString() + ":" + invId + ":" + firstTestPass + ":" +
                "shp_url=" + url + ":shp_user=" + userHash).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx?" +
                "MerchantLogin=" + merchantLogin +
                "&OutSum=" + sum.toString() +
                "&InvoiceID=" + invId +
                "&Description=" + decription +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&Culture=ru" +
                "&IsTest=1" +
                "&shp_url=" + url +
                "&shp_user=" + userHash;
    }

    public Long getNextPaymentId() {
        return Long.valueOf(entityManager
                .createNativeQuery("select nextval('order_id_seq')")
                .getSingleResult()
                .toString());
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Integer changeTransactionsStatus(Long orderId, byte status, String response) {
        int rows = transactionRepository.changeTransactionsStatus(orderId, status, response);
        return rows;
    }

    public Page<TransactionDto> getPageOfTransactionDtoList(BookStoreUser user, String sort, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        if (sort.equals("") || sort.equals("asc")) {
            return transactionRepository.findTransactionsByUserOrderByTimeAsc(user, nextPage)
                    .map(MappingUtils::mapToTransactionDto);
        } else {
            return transactionRepository.findTransactionsByUserOrderByTimeDesc(user, nextPage)
                    .map(MappingUtils::mapToTransactionDto);
        }
    }

    public Double getBalance(BookStoreUser user) {
        Double sum = transactionRepository.getTransactionSum(user);
        sum = sum == null ? 0 : sum;
        return sum;
    }

    private double getTotalSum(List<Book> booksList) {
        return booksList
                .stream()
                .mapToDouble(Book::getPriceWithDiscount)
                .sum();
    }
}
