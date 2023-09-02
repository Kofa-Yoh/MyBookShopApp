package com.example.MyBookShopApp.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDto {

    private String name;
    private String email;
    private String phone;
    private Set<UserRoleType> roles = new HashSet<>();

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }

    public UserDto() {
        this.name = "Пользователь";
        this.email = "";
        this.phone = "";
        this.roles.add(UserRoleType.ANONYMOUS);
    }

    public UserDto(String name, String email, String phone, Set<UserRoleType> roles) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public Boolean hasRole(String role){
        return roles.contains(UserRoleType.valueOf(role));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<UserRoleType> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRoleType> roles) {
        this.roles.addAll(roles);
    }
}
