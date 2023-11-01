package com.example.MyBookShopApp.security.verification;

import org.springframework.data.repository.CrudRepository;

public interface UserChangesRepository extends CrudRepository<UserChanges, Long> {

    UserChanges findUserChangesById(Long id);
}
