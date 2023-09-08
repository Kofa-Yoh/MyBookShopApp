package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookStoreUserDetails;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MappingUtils {

    public static UserDto mapToUserDto(BookStoreUserDetails entity){
        UserDto dto = new UserDto();
        dto.setName(entity.getBookStoreUser().getName());
        dto.setEmail(entity.getBookStoreUser().getEmail());
        dto.setPhone(entity.getBookStoreUser().getPhone());
        dto.setRoles(entity.getAuthorities().stream().map(a->UserRoleType.getRoleType(a.toString())).collect(Collectors.toSet()));
        return dto;
    }
}
