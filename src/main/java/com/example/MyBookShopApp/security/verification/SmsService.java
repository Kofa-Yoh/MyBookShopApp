package com.example.MyBookShopApp.security.verification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${twilio.AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${twilio.TWILIO_NUMBER}")
    private String TWILIO_NUMBER;

    private final SmsCodeRepository smsCodeRepository;

    public String sendSecretCodeSms(String contact) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String formattedContact = contact.replaceAll("[ ()-]", "");
        String genertedCode = generateCode();
        Message.creator(new PhoneNumber(formattedContact),
                new PhoneNumber(TWILIO_NUMBER),
                "Your secret code is: " + genertedCode
        ).create();
        return genertedCode;
    }

    public String generateCode() {
        //nnn nnn - pattern
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }

    public void saveNewCode(SmsCode smsCode) {
        if (smsCodeRepository.findByCode(smsCode.getCode()) == null) {
            smsCodeRepository.save(smsCode);
        }
    }

    public Boolean verifyCode(String code) {
        SmsCode smsCode = smsCodeRepository.findByCode(code);
        return (smsCode != null && !smsCode.isExpired());
    }
}
