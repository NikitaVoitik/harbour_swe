package com.example.harbour_swer.review;

import com.example.harbour_swer.book.Book;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReviewManager {
    @Getter
    private final Map<Book, Review> reviews = new HashMap<>();
    @Setter
    private Book featuredBook;

    public void addReview(Book book, Review review) {
        reviews.put(book, review);
    }

    public Review getReview(Book book) {
        return reviews.get(book);
    }
}
