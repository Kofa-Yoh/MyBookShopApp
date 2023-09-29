INSERT INTO public.book2user_type (id, code, name) VALUES (1, 'KEPT', 'Отложена');
INSERT INTO public.book2user_type (id, code, name) VALUES (2, 'CART', 'В корзине');
INSERT INTO public.book2user_type (id, code, name) VALUES (3, 'PAID', 'Куплена');
INSERT INTO public.book2user_type (id, code, name) VALUES (4, 'ARCHIVED', 'В архиве');

INSERT INTO public.book2user (id, time, book_id, type_id, user_id) VALUES (1, '2023-09-30 00:50:16.000000', 1, 2, 1);
