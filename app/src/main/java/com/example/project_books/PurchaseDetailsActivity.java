package com.example.project_books;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PurchaseDetailsActivity extends AppCompatActivity {

    private TextView bookTitleTextView;
    private TextView purchaseInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        // Initialize UI elements
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        purchaseInstructionsTextView = findViewById(R.id.purchaseInstructionsTextView);

        // Get book data from intent
        String bookTitle = getIntent().getStringExtra("bookTitle");

        // Set book title and instructions
        bookTitleTextView.setText(bookTitle);
        purchaseInstructionsTextView.setText("Enter your purchase details here...");
    }
}
