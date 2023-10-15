package com.example.MyBookShopApp.books.genre;

import com.example.MyBookShopApp.books.books.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Genre root;

    @OneToMany(mappedBy = "root")
    @JsonIgnore
    private Set<Genre> children;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Book> books;

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                ", children=" + children.toString() +
                '}';
    }
}
