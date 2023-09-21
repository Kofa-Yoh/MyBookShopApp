package com.example.MyBookShopApp.books.reviews;

public class BookReviewDto {

    private Integer id;
    private String userName;
    private String review;
    private String createTime;
    private Integer likesCount;
    private Integer dislikesCount;
    private Integer rating;

    private Byte userLike;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(Integer dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Byte getUserLike() {
        return userLike;
    }

    public void setUserLike(Byte userLike) {
        this.userLike = userLike;
    }

    public static int compareByRatingSortedAlgorithm(BookReviewDto r1, BookReviewDto r2) {
        return Integer.compare(r1.likesCount - r1.dislikesCount, r2.likesCount - r2.dislikesCount);
    }
}
