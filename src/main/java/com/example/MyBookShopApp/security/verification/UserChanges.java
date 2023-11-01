package com.example.MyBookShopApp.security.verification;

import com.example.MyBookShopApp.security.BookStoreUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class UserChanges {

    private static long HOURS_BEFORE_EXPIRATION = 24L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id", nullable = false)
    private BookStoreUser user;
    private String name;
    private String email;
    private String phone;
    private String password;
    private LocalDateTime expireTime;

    public UserChanges() {
        this.expireTime = LocalDateTime.now().plusHours(HOURS_BEFORE_EXPIRATION);
    }
}
