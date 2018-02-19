package com.example.android.popmovies.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.models.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String TRRAILER = "Trailer";

    private ArrayList<Trailer> trailers;

    private final TrailerAdapterOnClickHandler clickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    public interface TrailerAdapterOnClickHandler {
        void onTrailerItemClick(String trailerKey);
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        TrailerAdapterViewHolder holder = new TrailerAdapterViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        int trailNum = position + 1;
        holder.trailerTextView.setText(TRRAILER + " " + trailNum);
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView playTrailer;
        TextView trailerTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            playTrailer = (ImageView) itemView.findViewById(R.id.play_trailer_image_view);
            trailerTextView = (TextView) itemView.findViewById(R.id.trailer_text_view);
            playTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickHandler.onTrailerItemClick(trailers.get(getAdapterPosition()).getKey());
                }
            });
        }
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}