package com.example.MyBookShopApp.security.verification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_keys")
@Getter
@Setter
@NoArgsConstructor
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime expireTime;

    public SmsCode(String code, Integer expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
