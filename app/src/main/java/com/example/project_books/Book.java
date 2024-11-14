package com.example.project_books;

public class Book {
    private String id;
    private String title;
    private String author;
    private String price; // String type for price
    private String publisherId;

    // Constructor that initializes all fields
    public Book(String id, String title, String author, String price, String publisherId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publisherId = publisherId;
    }

    // Empty constructor for Firebase
    public Book() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }
}
