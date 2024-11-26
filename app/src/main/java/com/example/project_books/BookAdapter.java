package com.example.project_books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;




import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }


    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.priceTextView.setText(String.valueOf(book.getPrice()));

        // Load book image
        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.bookImageView);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, priceTextView;
        ImageView bookImageView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
        }
    }
}
