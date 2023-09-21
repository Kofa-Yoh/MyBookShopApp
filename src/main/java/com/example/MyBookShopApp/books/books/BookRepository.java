package com.example.MyBookShopApp.books.books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByBook2Authors_Author_Name(String name);

    @Query("from Book")
    List<Book> customFindAllBooks();

//    List<Book> findBooksByAuthorNameContaining(String authorName);
    List<Book> findBooksByBook2Authors_Author_NameContaining(String authorName);

    List<Book> findBooksByBook2Authors_Author_Slug(String authorSlug);

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceOldBetween(Integer min, Integer max);

    List<Book> findBooksByPriceOldIs(Integer price);

    @Query("from Book where isBestseller = 1")
    List<Book> getBestsellers();

    @Query(nativeQuery = true, value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) from books)")
    List<Book> getBooksWithMaxDiscount();

    Page<Book> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    Page<Book> findAllByOrderByPubDateDesc(Pageable nextPage);

    Page<Book> findByPubDateBetween(Date begin, Date end, Pageable nextPage);

    @Query("from Book where isBestseller = 1")
    Page<Book> getPopularBooks(Pageable nextPage);

    Page<Book> findBooksByBook2Authors_Author_Slug(String authorSlug, Pageable nextPage);

    Page<Book> findBookByTags_Name(String tag, Pageable nextPage);

    Page<Book> findBookByGenres_Name(String genre, Pageable nextPage);

    Book findBookBySlug(String slug);

    List<Book> findBooksBySlugIn(ArrayList<String> slugs);
}