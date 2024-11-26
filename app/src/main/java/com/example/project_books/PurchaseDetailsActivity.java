package com.example.project_books;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PurchaseDetailsActivity extends AppCompatActivity {

    private TextView bookTitleTextView, bookAuthorTextView, bookPriceTextView, rentalAmountTextView;
    private EditText userNameEditText, userAddressEditText, userPhoneEditText, rentDaysEditText;
    private Button purchaseButton;
    private RadioGroup radioGroup;
    private RadioButton radioPurchase, radioRent;

    private String bookTitle, bookAuthor, bookPrice;
    private String purchaseType;
    private double rentalAmount;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        // Initialize views
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        bookPriceTextView = findViewById(R.id.bookPriceTextView);
        rentalAmountTextView = findViewById(R.id.rentalAmountTextView);
        userNameEditText = findViewById(R.id.userNameEditText);
        userAddressEditText = findViewById(R.id.userAddressEditText);
        userPhoneEditText = findViewById(R.id.userPhoneEditText);
        rentDaysEditText = findViewById(R.id.rentDaysEditText);
        purchaseButton = findViewById(R.id.purchaseButton);
        radioGroup = findViewById(R.id.radioGroup);
        radioPurchase = findViewById(R.id.radioPurchase);
        radioRent = findViewById(R.id.radioRent);

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("purchases");

        // Retrieve the book details passed from MainActivity
        bookTitle = getIntent().getStringExtra("BOOK_TITLE");
        bookAuthor = getIntent().getStringExtra("BOOK_AUTHOR");
        bookPrice = getIntent().getStringExtra("BOOK_PRICE");

        // Set the book details to the UI
        bookTitleTextView.setText(bookTitle);
        bookAuthorTextView.setText(bookAuthor);
        bookPriceTextView.setText("Price: " + bookPrice);

        // Set the onClick listener for the purchase button
        purchaseButton.setOnClickListener(view -> submitPurchase());

        // Set up the radio group listener to show the rental duration input
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (radioRent.isChecked()) {
                rentDaysEditText.setVisibility(View.VISIBLE);
                rentalAmountTextView.setVisibility(View.VISIBLE);
            } else {
                rentDaysEditText.setVisibility(View.GONE);
                rentalAmountTextView.setVisibility(View.GONE);
            }
        });

        // Add a TextWatcher to dynamically update rental amount when rent days change
        rentDaysEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Update rental amount dynamically
                if (radioRent.isChecked()) {
                    updateRentalAmount();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed
            }
        });
    }

    // Method to handle purchase submission
    private void submitPurchase() {
        String name = userNameEditText.getText().toString();
        String address = userAddressEditText.getText().toString();
        String phone = userPhoneEditText.getText().toString();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Determine whether it's a purchase or rent
            if (radioPurchase.isChecked()) {
                purchaseType = "Purchase";
                rentalAmount = 0;  // No rental charge for purchases
            } else if (radioRent.isChecked()) {
                purchaseType = "Rent";
                String rentDaysString = rentDaysEditText.getText().toString();
                if (rentDaysString.isEmpty()) {
                    Toast.makeText(this, "Please enter number of rental days", Toast.LENGTH_SHORT).show();
                    return;
                }
                int rentDays = Integer.parseInt(rentDaysString);
                double price = Double.parseDouble(bookPrice);
                rentalAmount = price + (price * 0.01 * rentDays);  // Example rental rate: 10% of the price per day
            } else {
                Toast.makeText(this, "Please select an option: Purchase or Rent", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current date and time
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // Create a Purchase object with the details
            Purchase purchase = new Purchase(bookTitle, bookAuthor, bookPrice, name, address, phone, purchaseType, rentalAmount, currentDateTime);

            // Save to Firebase
            String purchaseId = dbRef.push().getKey();
            if (purchaseId != null) {
                dbRef.child(purchaseId).setValue(purchase)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Purchase/Rental Successful!", Toast.LENGTH_SHORT).show();
                                finish();  // Close the activity
                            } else {
                                Toast.makeText(this, "Failed to save details. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // Method to update rental amount based on rent days
    private void updateRentalAmount() {
        String rentDaysString = rentDaysEditText.getText().toString();
        if (!rentDaysString.isEmpty()) {
            int rentDays = Integer.parseInt(rentDaysString);
            double price = Double.parseDouble(bookPrice);
            rentalAmount = price + (price * 0.01 * rentDays);
            int cost = (int) (rentalAmount - price);
            rentalAmountTextView.setText("The price of the book " +price+" will be refunded once the book is returned so the cost will be actually "+cost+ "/-\n\n"+"Rental Amount: " + String.format(Locale.getDefault(), "%.2f", rentalAmount));
        }
    }
}
