package com.example.harbour_swer;

import com.example.harbour_swer.book.Book;
import com.example.harbour_swer.book.BookCatalog;
import com.example.harbour_swer.review.Review;
import com.example.harbour_swer.review.ReviewManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.min;


public class DataInitializer {
    private final BookCatalog bookCatalog;
    private final ReviewManager reviewManager;

    @Autowired
    public DataInitializer(BookCatalog bookCatalog, ReviewManager reviewManager) {
        System.out.println("Called DataInitializer constructor");
        this.bookCatalog = bookCatalog;
        this.reviewManager = reviewManager;
    }

    public void initData() {
        System.out.println("Called initData");
        List<Book> books = Arrays.asList(
                new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565"),
                new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084"),
                new Book("1984", "George Orwell", "9780451524935")
        );
        for (Book book : books) {
            bookCatalog.addBook(book);
        }

        List<Review> reviews = Arrays.asList(
                new Review(5, "A great book!", "Alice"),
                new Review(4, "Quite good, but not the best.", "Bob"),
                new Review(2, "I didn't like it.", "Charlie")
        );
        for (int i = 0; i < min(books.size(), reviews.size()); i++) {
            reviewManager.addReview(books.get(i), reviews.get(i));
        }
    }
}
