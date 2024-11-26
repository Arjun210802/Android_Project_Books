package com.example.project_books;

public class Purchase {
    private String bookTitle;
    private String bookAuthor;
    private String bookPrice;
    private String userName;
    private String userAddress;
    private String userPhone;
    private String purchaseType;  // "Purchase" or "Rent"
    private double rentalAmount;  // Only applicable for rent
    private String dateTime;  // To store the timestamp of the purchase/rental

    // Constructor for Purchase class
    public Purchase(String bookTitle, String bookAuthor, String bookPrice, String userName, String userAddress, String userPhone, String purchaseType, double rentalAmount, String dateTime) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPrice = bookPrice;
        this.userName = userName;
        this.userAddress = userAddress;
        this.userPhone = userPhone;
        this.purchaseType = purchaseType;
        this.rentalAmount = rentalAmount;
        this.dateTime = dateTime;
    }

    // Getters and Setters

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public double getRentalAmount() {
        return rentalAmount;
    }

    public void setRentalAmount(double rentalAmount) {
        this.rentalAmount = rentalAmount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
