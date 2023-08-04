package com.example.MyBookShopApp.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findTagByBooksIsNotNullOrderById();

    @Query(value = "select max(q.kol) from (select count(*) as kol from books_tags b group by b.tag_id) q", nativeQuery = true)
    Long getMaxBooksCount();
}