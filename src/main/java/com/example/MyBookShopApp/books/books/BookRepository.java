package com.example.MyBookShopApp.books.books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Page<Book> findBooksByBook2Authors_Author_Slug(String authorSlug, Pageable nextPage);

    Page<Book> findBookByTags_Name(String tag, Pageable nextPage);

    Page<Book> findBookByGenres_Name(String genre, Pageable nextPage);

    Book findBookBySlug(String slug);

    List<Book> findBooksBySlugIn(List<String> slugs);

    @Query("from Book b" +
            " left join b.book2users s" +
            " left join s.linkType t" +
            " group by b.id" +
            " order by count(1) filter (where t.code = com.example.MyBookShopApp.books.usersbooks.Book2UserTypeDto.PAID) +\n" +
            "            0.7 * count(1) filter (where t.code = com.example.MyBookShopApp.books.usersbooks.Book2UserTypeDto.CART) +\n" +
            "            0.4 * count(1) filter (where t.code = com.example.MyBookShopApp.books.usersbooks.Book2UserTypeDto.KEPT) DESC")
    Page<Book> getBooksOrderedByPopularity(Pageable nextPage);

    @Query(nativeQuery = true, value = "SELECT * FROM fn_get_recommended_books_for_user(:user_id);")
    Page<Book> getBooksForUserOrderedByPubDateAndRating(@Param("user_id")Integer user_id, Pageable nextPage);

    @Query(nativeQuery = true, value = "SELECT * FROM fn_get_recommended_books_for_user(:user_id);")
    List<Book> getBooksListForUserOrderedByPubDateAndRating(@Param("user_id")Integer user_id);

    Book findBookById(int id);

    List<Book> findBooksByBook2users_User_Id(Integer user_id);
}
