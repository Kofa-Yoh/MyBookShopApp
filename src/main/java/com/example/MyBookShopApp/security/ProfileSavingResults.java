package com.example.MyBookShopApp.security;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSavingResults {

    private Boolean result;
    private String errors = "";
    private BookStoreUser user = new BookStoreUser();
}
