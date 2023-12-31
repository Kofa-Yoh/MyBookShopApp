package com.example.MyBookShopApp.security;

public enum UserRoleType {
    USER, ANONYMOUS;

    public static UserRoleType getRoleType(String authority){
        String role = authority.replace("ROLE_", "");
        return UserRoleType.valueOf(role);
    }
}
