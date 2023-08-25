package com.example.MyBookShopApp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "Book object")
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value="id", required=true, index = 1)
    @Schema(description = "Unique identifier of the Book, generated by db automatically",
            example = "1")
    private Integer id;

    @Column(name = "pub_date")
    @Schema(description = "Date of the book publication")
    private Date pubDate;

    @Column(name = "is_bestseller")
    @Schema(description = "1 - the book is considered to be bestseller,\n 0 - the book not a bestseller")
    private Integer isBestseller;

    @Schema(description = "Mnemonical identity sequence of characters",
            format = "book-@@@-###",
            example = "book-wly-166")
    private String slug;

    @JsonProperty(value="title", required=true)
    @Schema(description = "Name of the title",
            example = "Java")
    private String title;

    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    @JsonProperty(value = "price")
    private Integer priceOld;

    @Column(name = "discount")
    @JsonProperty(value = "discount")
    private Double price;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    Set<Book2Author> book2Authors;

    @OneToOne(mappedBy = "book")
    @JsonIgnore
    private BooksStatistic statistic;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "books_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;

    @JsonProperty(value = "discountPercent")
    public Integer getDiscountPercent(){
        return (int) Math.floor(price * 100);
    }

    @JsonProperty(value = "discountPrice")
    public Integer getPriceWithDiscount(){
        return Math.toIntExact(Math.round(priceOld * (1 - price)));
    }

    @JsonProperty(value = "authors")
    public String getBookAuthors(){
        return book2Authors.stream()
                .sorted(Comparator.comparing(Book2Author::getSortIndex))
                .map(book2Author -> book2Author.getAuthor().getName())
                .collect(Collectors.joining(", "));
    }

    @JsonProperty(value = "authorsList")
    public List<Author> getBookAthorsList(){
        return book2Authors.stream()
                .sorted(Comparator.comparing(Book2Author::getSortIndex))
                .map(book2Author -> book2Author.getAuthor())
                .toList();
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", priceOld='" + priceOld + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Integer getIsBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(Integer isBestseller) {
        this.isBestseller = isBestseller;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(Integer priceOld) {
        this.priceOld = priceOld;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<Book2Author> getBook2Authors() {
        return book2Authors;
    }

    public void setBook2Authors(Set<Book2Author> book2Authors) {
        this.book2Authors = book2Authors;
    }

    public BooksStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(BooksStatistic statistic) {
        this.statistic = statistic;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }
}
