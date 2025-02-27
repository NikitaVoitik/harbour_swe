package com.example.harbour_swer;

import com.example.harbour_swer.book.Book;
import com.example.harbour_swer.book.BookCatalog;
import com.example.harbour_swer.review.ReviewManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
@ComponentScan("com.example.harbour_swer")
public class LibraryConfig {

    @Bean
    public Book book1() {
        System.out.println("AppConfig: Creating book1 bean using @Bean method");
        return new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565");
    }

    @Bean
    public Book book2() {
        System.out.println("AppConfig: Creating book2 bean using @Bean method");
        return new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084");
    }

    @Bean
    public Book book3() {
        System.out.println("AppConfig: Creating book3 bean using @Bean method");
        return new Book("1984", "George Orwell", "9780451524935");
    }

    @Bean
    public DataInitializer dataInitializer(BookCatalog bookCatalog, ReviewManager reviewManager) {
        reviewManager.setFeaturedBook(book1());
        return new DataInitializer(bookCatalog, reviewManager);
    }
}
