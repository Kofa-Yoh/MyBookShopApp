CREATE OR REPLACE FUNCTION fn_get_recommended_books_for_user(num INT)
    RETURNS setof books
AS $$
with z as (select q.*, row_number() over () as row_num
             from (select b.*
                    from books b
                    left join book2author a on b.id = a.book_id
                    left join books_tags t on b.id = t.book_id
                    left join books_genres g on b.id = g.book_id
                    left join (select bu2.book_id, ba2.author_id
                                 from book2user bu2
                                 join book2author ba2 on bu2.book_id = ba2.book_id
                                where bu2.user_id = num) b2 on b2.author_id = a.author_id
                    left join (select bu3.book_id, bt3.tag_id
                                 from book2user bu3
                                 join books_tags bt3 on bu3.book_id = bt3.book_id
                                where bu3.user_id = num) b3 on b3.tag_id = t.tag_id
                    left join (select bu4.book_id, bg4.genre_id
                                from book2user bu4
                                join books_genres bg4 on bu4.book_id = bg4.book_id
                               where bu4.user_id = num) b4 on b4.genre_id = g.genre_id
                    where (b2.book_id != b.id or b2.book_id is null)
                      and (b3.book_id != b.id or b3.book_id is null)
                      and (b4.book_id != b.id or b4.book_id is null)
                    order by case when b2.author_id is null then 1 else 0 end
                            ,case when b3.tag_id is null then 1 else 0 end
                            ,case when b4.genre_id is null then 1 else 0 end
                            ,(select coalesce(avg(ba.rate), 0) from book_assesment ba where ba.book_id = b.id) desc
                            ,b.pub_date desc
                    ) q)
select discount, id, is_bestseller, price, pub_date, description, image, slug, title
  from z as z1
 where z1.id not in (select z2.id from z as z2 where z2.id = z1.id and z2.row_num > z1.row_num);
$$
    LANGUAGE sql