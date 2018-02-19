package com.example.android.popmovies.adapters;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.android.popmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;
import com.example.android.popmovies.models.Movie;

import com.example.android.popmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMoviesAdapterViewHolder> {
    private Cursor mCursor;
    private final FavoriteMovieAdapterOnClickHandler clickHandler;

    public FavoriteMoviesAdapter(FavoriteMovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    public interface FavoriteMovieAdapterOnClickHandler {
        void onFavoriteMovieItemClick(int cursorPosition);
    }

    @Override
    public FavoriteMoviesAdapter.FavoriteMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new FavoriteMoviesAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviesAdapter.FavoriteMoviesAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String moviePosterImagePath = mCursor.getString(mCursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH));

        loadImage(moviePosterImagePath , holder);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class FavoriteMoviesAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;

        public FavoriteMoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieImageView = (ImageView) itemView.findViewById(R.id.image_view_movie_poster);
           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickHandler.onFavoriteMovieItemClick(getAdapterPosition());
                }
            });
        }
    }
    private void loadImage(String moviePosterPath , FavoriteMoviesAdapterViewHolder holder){
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
    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
