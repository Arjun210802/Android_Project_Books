package com.example.project_books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout bookListLayout;
    private DatabaseReference dbRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference for "books" collection
        dbRef = FirebaseDatabase.getInstance().getReference("books");

        // Get the current user ID if the user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }

        // Initialize the LinearLayout where books will be listed
        bookListLayout = findViewById(R.id.bookListLayout);

        // Button to navigate to SellBookActivity
        Button sellBookButton = findViewById(R.id.sellBookButton);
        sellBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSellBook();
            }
        });

        // Buttons for viewing all books or only the user's published books
        Button allBooksButton = findViewById(R.id.allBooksButton);
        Button myBooksButton = findViewById(R.id.myBooksButton);

        allBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllBooks();
            }
        });

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId != null) {
                    loadUserPublishedBooks(currentUserId);
                }
            }
        });

        // Load all books by default on opening the activity
        loadAllBooks();
    }

    // Method to load all books from the Firebase database
    private void loadAllBooks() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookListLayout.removeAllViews(); // Clear previous views
                List<Book> allBooks = new ArrayList<>();

                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (book != null) {
                        allBooks.add(book);
                        addBookView(book); // Add book to LinearLayout
                    }
                }
                Log.d("MainActivity", "Loaded " + allBooks.size() + " books");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Failed to load books: " + error.getMessage());
            }
        });
    }

    // Method to load only the books published by the current user
    private void loadUserPublishedBooks(String userId) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookListLayout.removeAllViews(); // Clear previous views
                List<Book> userBooks = new ArrayList<>();

                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (book != null && userId.equals(book.getPublisherId())) {
                        userBooks.add(book);
                        addBookView(book); // Add book to LinearLayout
                    }
                }
                Log.d("MainActivity", "Loaded " + userBooks.size() + " books for user " + userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Failed to load user books: " + error.getMessage());
            }
        });
    }

    // Method to create and add a TextView for each book in the LinearLayout
    private void addBookView(Book book) {
        View bookView = getLayoutInflater().inflate(R.layout.item_book, bookListLayout, false);

        TextView titleTextView = bookView.findViewById(R.id.titleTextView);
        TextView authorTextView = bookView.findViewById(R.id.authorTextView);
        TextView priceTextView = bookView.findViewById(R.id.priceTextView);

        // Set the book details
        titleTextView.setText(book.getTitle());
        authorTextView.setText("Author: " + book.getAuthor());
        priceTextView.setText("Price: ₹ " + book.getPrice());

        // Set a click listener to go to purchase details
        bookView.setOnClickListener(v -> goToPurchaseDetails(book));

        // Add the card view to the book list layout
        bookListLayout.addView(bookView);
    }


    // Method to navigate to SellBookActivity
    private void goToSellBook() {
        Intent intent = new Intent(MainActivity.this, SellBookActivity.class);
        startActivity(intent);
    }

    // Method to navigate to PurchaseDetailsActivity
    private void goToPurchaseDetails(Book book) {
        Intent intent = new Intent(MainActivity.this, PurchaseDetailsActivity.class);
        intent.putExtra("bookId", book.getId()); // Pass book ID
        intent.putExtra("bookTitle", book.getTitle()); // Pass book title
        startActivity(intent);
    }
}
