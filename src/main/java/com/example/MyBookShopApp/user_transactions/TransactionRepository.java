package com.example.MyBookShopApp.user_transactions;

import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Transactional
    @Modifying
    @Query("update Transaction t set t.status = :status where t.orderId = :orderId")
    int changeTransactionsStatus(@Param("orderId") Long orderId, @Param("status") byte status);

    @Transactional
    @Modifying
    @Query("update Transaction t set t.status = :status, t.response = :response where t.orderId = :orderId")
    int changeTransactionsStatus(@Param("orderId") Long orderId, @Param("status") byte status, @Param("response") String response);

    Page<Transaction> findTransactionsByUserOrderByTimeAsc(BookStoreUser user, Pageable nextPage);

    Page<Transaction> findTransactionsByUserOrderByTimeDesc(BookStoreUser user, Pageable nextPage);

    @Query("select sum(t.value) from Transaction t where t.user = :user and t.status = 1")
    Double getTransactionSum(@Param("user") BookStoreUser user);
}
