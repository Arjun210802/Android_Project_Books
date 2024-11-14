package com.example.project_books;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;
    private final List<Book> bookList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        RecyclerView bookRecyclerView = findViewById(R.id.adminBookRecyclerView);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up adapter with the book list
        bookAdapter = new BookAdapter(bookList);
        bookRecyclerView.setAdapter(bookAdapter);


        loadBooks();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadBooks() {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear(); // Clear list before adding new items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class); // Convert document to Book object
                            bookList.add(book); // Add book to the list
                        }
                        bookAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    } else {
                        Toast.makeText(this, "Failed to load books", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
