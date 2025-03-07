package com.example.harbour_swer.book;

public class Book {
    private String title;
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) {
        System.out.println("Called Book constructor");
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
