package com.example.MyBookShopApp.user_transactions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransactionPageDto {

    private Integer count;

    private List<TransactionDto> transactions;

    public TransactionPageDto(List<TransactionDto> transactions) {
        this.transactions = transactions;
        this.count = transactions.size();
    }
}
