package com.example.harbour_swer.review;

public class Review {
    private int rating;
    private String comment;
    private String reviewer;

    public Review(int rating, String comment, String reviewer) {
        System.out.println("Called Review constructor");
        this.rating = rating;
        this.comment = comment;
        this.reviewer = reviewer;
    }

    public String toString(){
        return "Review{" +
                "rating='" + rating + '\'' +
                ", comment='" + comment + '\'' +
                ", reviewer='" + reviewer + '\'' +
                '}';
    }
}
