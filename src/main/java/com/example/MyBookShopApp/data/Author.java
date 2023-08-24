package com.example.MyBookShopApp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String photo;

    @Schema(description = "Mnemonical identity sequence of characters for url",
            format = "author-{name}-###")
    private String slug;

    private String name;

    @Schema(description = "Biography")
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    Set<Book2Author> book2Authors;

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

    public Set<Book2Author> getBook2Authors() {
        return book2Authors;
    }

    public void setBook2Authors(Set<Book2Author> book2Authors) {
        this.book2Authors = book2Authors;
    }
}
