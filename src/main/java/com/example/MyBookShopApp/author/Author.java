package com.example.MyBookShopApp.author;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "authors")
@Data
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

    public String getDescriptionSentences(int begin, int end) {
        int countSentences = StringUtils.countMatches(description, ".");
        if (countSentences < begin || begin > end) return "";
        countSentences = countSentences > end ? end : countSentences;
        return String.join(". ",
                Arrays.stream(StringUtils.split(description, "."))
                        .toList()
                        .subList(begin, end)) + ".";
    }
}
