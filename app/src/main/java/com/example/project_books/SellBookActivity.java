package com.example.project_books;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellBookActivity extends AppCompatActivity {

    private EditText bookTitleEditText, bookAuthorEditText, bookPriceEditText;
    private DatabaseReference dbRef;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_book);

        // Initialize Firebase Database reference for "books" collection
        dbRef = FirebaseDatabase.getInstance().getReference("books");

        // Get the current authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if no user is signed in
            Toast.makeText(this, "Please sign in to add a book", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize EditText fields for book title, author, and price
        bookTitleEditText = findViewById(R.id.bookTitleEditText);
        bookAuthorEditText = findViewById(R.id.bookAuthorEditText);
        bookPriceEditText = findViewById(R.id.bookPriceEditText);

        // Button to submit book details
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });
    }

    private void addBook() {
        String title = bookTitleEditText.getText().toString().trim();
        String author = bookAuthorEditText.getText().toString().trim();
        String priceText = bookPriceEditText.getText().toString().trim();

        // Validate the title, author, and price inputs
        if (title.isEmpty()) {
            bookTitleEditText.setError("Title is required");
            return;
        }
        if (author.isEmpty()) {
            bookAuthorEditText.setError("Author is required");
            return;
        }
        if (priceText.isEmpty()) {
            bookPriceEditText.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);  // Parse as double
        } catch (NumberFormatException e) {
            bookPriceEditText.setError("Enter a valid price");
            return;
        }

        // Create a unique book ID and new Book object
        String bookId = dbRef.push().getKey();
        if (bookId != null) {
            // Use String.valueOf(price) if price in Book class is of String type
            Book book = new Book(bookId, title, author, String.valueOf(price), FirebaseAuth.getInstance().getCurrentUser().getUid());

            dbRef.child(bookId).setValue(book).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SellBookActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SellBookActivity.this, "Failed to add book. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}