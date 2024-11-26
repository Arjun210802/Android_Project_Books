package com.example.project_books;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class SellBookActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText bookTitleEditText, bookAuthorEditText, bookPriceEditText;
    private ImageView bookImageView;
    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference dbRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_book);

        // Firebase initialization
        dbRef = FirebaseDatabase.getInstance().getReference("books");
        storageRef = FirebaseStorage.getInstance().getReference("book_images");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to add a book.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find views
        bookTitleEditText = findViewById(R.id.bookTitleEditText);
        bookAuthorEditText = findViewById(R.id.bookAuthorEditText);
        bookPriceEditText = findViewById(R.id.bookPriceEditText);
        bookImageView = findViewById(R.id.bookImageView);

        // Select image button
        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Submit book details button
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> addBook());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Book Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bookImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addBook() {
        String title = bookTitleEditText.getText().toString().trim();
        String author = bookAuthorEditText.getText().toString().trim();
        String priceText = bookPriceEditText.getText().toString().trim();

        if (validateInputs(title, author, priceText)) {
            if (imageUri != null) {
                String imageId = UUID.randomUUID().toString();
                StorageReference fileRef = storageRef.child(imageId);

                fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> saveBookToDatabase(uri.toString(), title, author, priceText))
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to retrieve image URL.", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Image upload failed. Try again.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        });
            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs(String title, String author, String priceText) {
        if (TextUtils.isEmpty(title)) {
            bookTitleEditText.setError("Title is required");
            return false;
        }
        if (TextUtils.isEmpty(author)) {
            bookAuthorEditText.setError("Author is required");
            return false;
        }
        if (TextUtils.isEmpty(priceText)) {
            bookPriceEditText.setError("Price is required");
            return false;
        }
        return true;
    }

    private void saveBookToDatabase(String imageUrl, String title, String author, String priceText) {
        String bookId = dbRef.push().getKey();
        if (bookId != null) {
            Book book = new Book(bookId, title, author, priceText, currentUser.getUid(), imageUrl);
            dbRef.child(bookId).setValue(book).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Book added successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SellBookActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add book.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
