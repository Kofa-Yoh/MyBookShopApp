package com.example.MyBookShopApp.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDto {

    private String name;
    private String email;
    private String phone;
    private String hash;
    private Set<UserRoleType> roles = new HashSet<>();

    public UserDto() {
        this.name = "Пользователь";
        this.email = "";
        this.phone = "";
        this.hash = UUID.randomUUID().toString();
        this.roles.add(UserRoleType.ANONYMOUS);
    }

    public UserDto(String name, String email, String phone, UUID hash, Set<UserRoleType> roles) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.hash = hash.toString();
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public Boolean hasRole(String role){
        return roles.contains(UserRoleType.valueOf(role));
    }
}
