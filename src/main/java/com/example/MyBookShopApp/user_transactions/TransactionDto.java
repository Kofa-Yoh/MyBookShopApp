package com.example.MyBookShopApp.user_transactions;

import com.example.MyBookShopApp.books.books.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openqa.selenium.By;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String time;
    private String sum;
    private String description;
    private Long orderId;
    private Byte status;
    private Book book;
}
