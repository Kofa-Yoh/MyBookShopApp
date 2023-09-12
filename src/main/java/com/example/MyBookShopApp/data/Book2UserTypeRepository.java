package com.example.MyBookShopApp.data;

import org.springframework.data.repository.CrudRepository;

public interface Book2UserTypeRepository extends CrudRepository<Book2UserType, Integer> {

    Book2UserType findByCode(Book2UserTypeDto code);
}
