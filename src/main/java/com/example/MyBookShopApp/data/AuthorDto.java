package com.example.MyBookShopApp.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class AuthorDto {

    private Integer id;
    private String photo;
    private String slug;
    private String name;
    private String description;

    public String getDescriptionSentences(int begin) {
        int countSentences = StringUtils.countMatches(description, ".");
        return getDescriptionSentences(begin, countSentences);
    }

    public String getDescriptionSentences(int begin, int end){
        int countSentences = StringUtils.countMatches(description, ".");
        if (countSentences < begin || begin > end) return "";
        countSentences = countSentences > end ? end : countSentences;
        return String.join(". ",
                Arrays.stream(StringUtils.split(description, "."))
                        .toList()
                        .subList(begin, end)) + ".";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
