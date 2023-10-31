package com.example.MyBookShopApp.user_transactions;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id", nullable = false)
    private BookStoreUser user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    @Schema(defaultValue = "0")
    private Double value;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "book_id", nullable = true)
    private Book book;

    @Column(nullable = false)
    private String description;

    private Long orderId;

    @Schema(defaultValue = "0")
    @Column(nullable = false)
    private byte status;

    private String response;
}
