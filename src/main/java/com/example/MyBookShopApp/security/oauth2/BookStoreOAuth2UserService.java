package com.example.MyBookShopApp.security.oauth2;

import com.example.MyBookShopApp.security.AuthenticationType;
import com.example.MyBookShopApp.security.BookStoreUser;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class BookStoreOAuth2UserService extends DefaultOAuth2UserService {

    private final BookStoreUserRepository bookStoreUserRepository;

    public BookStoreOAuth2UserService(BookStoreUserRepository bookStoreUserRepository) {
        this.bookStoreUserRepository = bookStoreUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String clientName = userRequest.getClientRegistration().getClientName();
        OAuth2User user =  super.loadUser(userRequest);
        return new BookStoreOAuth2User(user, clientName);
    }

    public BookStoreUserDetails saveUserInDb(BookStoreOAuth2User oAuth2User) {
        BookStoreUser bookStoreUser = bookStoreUserRepository.findBookStoreUserByEmail(oAuth2User.getEmail());
        if (bookStoreUser == null) {
            BookStoreUser user = new BookStoreUser();
            user.setName(oAuth2User.getName());
            user.setEmail(oAuth2User.getEmail());
            user.setAuthType(AuthenticationType.valueOf(oAuth2User.getOauth2ClientName().toUpperCase()));
            bookStoreUserRepository.save(user);
            return new BookStoreUserDetails(user);
        } else {
            return new BookStoreUserDetails(bookStoreUser);
        }
    }
}
