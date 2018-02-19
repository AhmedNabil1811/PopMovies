package com.example.android.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMoviesData;
    private final MovieAdapterOnClickHandler clickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    public interface MovieAdapterOnClickHandler {
        void onMovieItemClick(Movie movie);
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        MovieAdapterViewHolder holder = new MovieAdapterViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String moviePosterPath = mMoviesData.get(position).getPosterImagePath();
        Context context = holder.movieImageView.getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Picasso.with(context)
                .load(moviePosterPath)
                .resize((int) (metrics.widthPixels / 2.0), (int) (metrics.heightPixels / 2.0))
                .centerCrop()
                .into(holder.movieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null)
            return 0;
        return mMoviesData.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            movieImageView = (ImageView) itemView.findViewById(R.id.image_view_movie_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickHandler.onMovieItemClick(mMoviesData.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setmMovieData(ArrayList<Movie> movieData) {
        mMoviesData = movieData;
        notifyDataSetChanged();
    }
}