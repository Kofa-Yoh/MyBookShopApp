package com.example.MyBookShopApp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="root_id")
    private Genre root;

    @OneToMany(mappedBy="root")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Genre getRoot() {
        return root;
    }

    public void setRoot(Genre root) {
        this.root = root;
    }

    public Set<Genre> getChildren() {
        return children;
    }

    public void setChildren(Set<Genre> children) {
        this.children = children;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
