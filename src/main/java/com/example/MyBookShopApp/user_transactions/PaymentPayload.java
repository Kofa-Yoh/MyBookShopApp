package com.example.MyBookShopApp.user_transactions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PaymentPayload {

    private String hash;
    private String sum;
    private Long time;
}
