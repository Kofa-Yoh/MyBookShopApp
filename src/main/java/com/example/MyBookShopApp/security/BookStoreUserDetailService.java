package com.example.MyBookShopApp.security;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookStoreUserDetailService implements UserDetailsService {

    private final BookStoreUserRepository bookStoreUserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s.contains("@")) {
            BookStoreUser bookStoreUser = bookStoreUserRepository.findBookStoreUserByEmail(s);
            if (bookStoreUser != null) {
                return new BookStoreUserDetails(bookStoreUser);
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        } else {
            BookStoreUser bookStoreUser = bookStoreUserRepository.findBookStoreUserByPhone(s);
            if (bookStoreUser != null) {
                return new PhoneNumberUserDetails(bookStoreUser);
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        }
    }
}
