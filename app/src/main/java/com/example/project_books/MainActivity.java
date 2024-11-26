package com.example.project_books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
        TextView userNameTextView = findViewById(R.id.userNameTextView);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userNameTextView.setText("Welcome, " + userName + "!");

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

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
                        addBookView(book,currentUserId); // Add book to LinearLayout
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
                        addBookView(book,currentUserId); // Add book to LinearLayout
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
    private void addBookView(Book book, String currentUserId) {
        View bookView = getLayoutInflater().inflate(R.layout.item_book, bookListLayout, false);

        TextView titleTextView = bookView.findViewById(R.id.titleTextView);
        TextView authorTextView = bookView.findViewById(R.id.authorTextView);
        TextView priceTextView = bookView.findViewById(R.id.priceTextView);
        ImageView bookImageView = bookView.findViewById(R.id.bookImageView);
        Button editButton = bookView.findViewById(R.id.editButton);
        Button deleteButton = bookView.findViewById(R.id.deleteButton);

        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        priceTextView.setText(book.getPrice());

        // Load the image using Glide or similar library
        Glide.with(this)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(bookImageView);

        // Show or hide buttons based on ownership
        if (book.getPublisherId().equals(currentUserId)) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(v -> openEditBookDialog(book));
            deleteButton.setOnClickListener(v -> deleteBook(book));
        } else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        // Add to parent layout
        bookListLayout.addView(bookView);
    }

    private void openEditBookDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_book, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.editTitleEditText);
        EditText authorEditText = dialogView.findViewById(R.id.editAuthorEditText);
        EditText priceEditText = dialogView.findViewById(R.id.editPriceEditText);

        titleEditText.setText(book.getTitle());
        authorEditText.setText(book.getAuthor());
        priceEditText.setText(book.getPrice());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedTitle = titleEditText.getText().toString().trim();
            String updatedAuthor = authorEditText.getText().toString().trim();
            String updatedPrice = priceEditText.getText().toString().trim();

            // Update book in Firebase
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(book.getId());
            bookRef.child("title").setValue(updatedTitle);
            bookRef.child("author").setValue(updatedAuthor);
            bookRef.child("price").setValue(updatedPrice).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Book updated successfully!", Toast.LENGTH_SHORT).show();
                    loadAllBooks(); // Reload the book list
                } else {
                    Toast.makeText(MainActivity.this, "Failed to update book.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBook(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Book");
        builder.setMessage("Are you sure you want to delete this book?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(book.getId());
            bookRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Book deleted successfully!", Toast.LENGTH_SHORT).show();
                    loadAllBooks(); // Reload the book list
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete book.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
