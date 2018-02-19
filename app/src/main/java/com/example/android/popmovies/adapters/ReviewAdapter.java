package com.example.android.popmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.models.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Review> reviews;

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        ReviewAdapterViewHolder holder = new ReviewAdapterViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {

        holder.authorTextView.setText(reviews.get(position).getAuthorName());
        holder.contentTextView.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView contentTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.author_text_view);
            contentTextView = (TextView) itemView.findViewById(R.id.content_text_view);
        }
    }
    public void setReviews(ArrayList<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
