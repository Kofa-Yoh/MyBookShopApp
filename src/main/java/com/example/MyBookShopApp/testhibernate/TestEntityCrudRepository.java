package com.example.MyBookShopApp.testhibernate;

import org.springframework.data.repository.CrudRepository;

public interface TestEntityCrudRepository extends CrudRepository<TestEntity, Long> {
}
