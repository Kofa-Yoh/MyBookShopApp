package com.example.MyBookShopApp.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BooksRatingAndPopularityRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByStatisticNotNullOrderByStatistic_BuyersCount();

    @Query("from Book b join BooksStatistic s on b.id = s.book.id where s.buyersCount + s.inCartCount + s.postponedCount > 0 order by s.buyersCount + 0.7 * s.inCartCount + 0.4 * s.postponedCount DESC")
    Page<Book> getBooksOrderedByPopularity(Pageable nextPage);
}
