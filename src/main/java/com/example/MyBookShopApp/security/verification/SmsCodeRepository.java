package com.example.MyBookShopApp.security.verification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    public SmsCode findByCode(String code);
}
