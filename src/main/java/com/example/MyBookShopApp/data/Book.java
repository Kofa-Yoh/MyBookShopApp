package com.example.MyBookShopApp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Integers;

import java.util.Date;

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

    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonIgnore
    private Author author;

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

    public Integer getDiscountPercent(){
        return (int) Math.floor(price * 100);
    }

    public Long getPriceWithDiscount(){
        return Math.round(priceOld * (1 - price));
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author=" + author +
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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
}
