package com.example.harbour_swer.book;

import com.example.harbour_swer.review.ReviewManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookCatalog {
    @Autowired
    private ReviewManager reviewManager;
    @Getter
    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }


}
