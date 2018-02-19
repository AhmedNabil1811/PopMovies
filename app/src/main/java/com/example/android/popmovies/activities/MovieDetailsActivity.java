package com.example.android.popmovies.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.R;
import com.example.android.popmovies.adapters.ReviewAdapter;
import com.example.android.popmovies.adapters.TrailerAdapter;
import com.example.android.popmovies.data.FavoriteMoviesContract;
import com.example.android.popmovies.models.Movie;
import com.example.android.popmovies.models.Review;
import com.example.android.popmovies.models.Trailer;
import com.example.android.popmovies.utils.NetworkUtils;
import com.example.android.popmovies.utils.ReviewNetworking;
import com.example.android.popmovies.utils.TrailerNetworking;
import com.squareup.picasso.Picasso;
import com.example.android.popmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.cacheColorHint;
import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.popmovies.R.string.reviews;
import static com.example.android.popmovies.R.string.trailers;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks {
    public static final String MOVIE_EXTRA_KEY = "movie_extra";
    public static final String FAVORITE_MOVIE_EXTRA = "favorite_movie";
    private static final int TRAILERS_LOADER = 300;
    private static final int REVIEWS_LOADER = 400;
    private static final String MOVIE_ID_EXTRA = "movie_id";

    private boolean isAfavoriteMovie;
    private Movie movie;


    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView movieRating;
    private TextView movieOverview;
    private ImageView moviePoster;
    private RecyclerView trailersRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private TrailerAdapter trailersAdapter;
    private ReviewAdapter reviewsAdapter;
    private TextView trailerStringTextView;
    private TextView reviewStringTextView;
    private Button favoriteButton;

    private static final String WEB_PAGE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        setupUi();

        recieveIntent();

        setupTrailersRecyclerView();

        setupReviewsRecyclerView();

        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_ID_EXTRA, movie.getId());
        getSupportLoaderManager().initLoader(TRAILERS_LOADER, bundle, this);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER, bundle, this);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAfavoriteMovie) {
                    removeFromFavoriteList();
                    isAfavoriteMovie = false;
                    favoriteButton.setText(R.string.action_add_to_list);

                } else {
                    addToFavoriteList();
                    isAfavoriteMovie = true;
                    favoriteButton.setText(R.string.action_remove_from_list);
                }
            }
        });
    }

    private void removeFromFavoriteList() {
        Uri uri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, Long.parseLong(movie.getId()));
        int id = getContentResolver().delete(uri, null, null);
        if (id == -1) {
            Toast.makeText(this, "Error with removing this movie from your favorite list !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Movie is deleted from your favorite list", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFavoriteList() {
        ContentValues values = new ContentValues();
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_ID, Integer.parseInt(movie.getId()));
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH, movie.getPosterImagePath());
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_RATING, Float.parseFloat(movie.getRating()));
        values.put(FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());

        Uri uri = getContentResolver().insert(FavoriteMoviesEntry.CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(this, "Error with saving this movie to your favorite list ! ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Movie is added to your favorite list", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupUi() {

        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieRating = (TextView) findViewById(R.id.movie_rating);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        moviePoster = (ImageView) findViewById(R.id.movie_poster);
        favoriteButton = (Button) findViewById(R.id.favorite_button);
        setButtonText();

        trailerStringTextView = (TextView) findViewById(R.id.trailer_string_tv);
        reviewStringTextView = (TextView) findViewById(R.id.review_string_tv);
    }

    private void setButtonText() {
        isAfavoriteMovie = getIntent().getBooleanExtra(FAVORITE_MOVIE_EXTRA, false);
        if (isAfavoriteMovie) {
            favoriteButton.setText(R.string.action_remove_from_list);
        } else {
            favoriteButton.setText(R.string.action_add_to_list);
        }
    }

    private void setupTrailersRecyclerView() {

        trailersRecyclerView = (RecyclerView) findViewById(R.id.trailer_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(manager);

        trailersAdapter = new TrailerAdapter(this);
        trailersRecyclerView.setAdapter(trailersAdapter);
    }

    private void setupReviewsRecyclerView() {
        reviewsRecyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(manager);

        reviewsAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(reviewsAdapter);
    }

    @Override
    public void onTrailerItemClick(String trailerKey) {
        String path = WEB_PAGE_URL + trailerKey;
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void recieveIntent() {
        if (getIntent() != null) {
            movie = getIntent().getParcelableExtra(MOVIE_EXTRA_KEY);
        }

        if (movie != null) {
            movieTitle.setText(movie.getTitle());
            movieReleaseDate.setText(movie.getReleaseDate().split("-")[0]);
            movieRating.setText(String.format("%s/10", movie.getRating()));
            movieOverview.setText(movie.getOverview());
            Picasso.with(this)
                    .load(movie.getPosterImagePath())
                    .into(moviePoster);
        }
    }

    private void updateUiWithReviews(ArrayList<Review> reviews) {
        if (reviews == null) {
            hideReviews(R.string.review_error);

        } else if (reviews.isEmpty()) {
            hideReviews(R.string.no_review_string);
        } else {
            showReviews();
            reviewsAdapter.setReviews(reviews);
        }
    }

    private void updateUiWithTrailers(ArrayList<Trailer> trailers) {
        if (trailers == null) {
            hideTrailers(R.string.trailer_error);
        } else if (trailers.isEmpty()) {
            hideTrailers(R.string.no_trailer_string);
        } else {
            showTrailers();
            trailersAdapter.setTrailers(trailers);
        }
    }


    private ArrayList<Review> loadReviews(String movieId) {
        ReviewNetworking reviewNetworking = new ReviewNetworking();
        URL url = reviewNetworking.creatUrl(movieId);
        String jasonResponse = NetworkUtils.getResponseFromHttpUrl(url);
        ArrayList<Review> reviews = reviewNetworking.extractDataFromJson(jasonResponse);
        return reviews;
    }

    private ArrayList<Trailer> loadTrailers(String movieId) {
        TrailerNetworking trailerNetworking = new TrailerNetworking();
        URL url = trailerNetworking.creatUrl(movieId);
        String jasonResponse = NetworkUtils.getResponseFromHttpUrl(url);
        ArrayList<Trailer> trailers = trailerNetworking.extractDataFromJson(jasonResponse);
        return trailers;
    }

    @Override
    public Loader onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Object loadInBackground() {
                String movieId = args.getString(MOVIE_ID_EXTRA);
                switch (id) {
                    case TRAILERS_LOADER:
                        return loadTrailers(movieId);
                    case REVIEWS_LOADER:
                        return loadReviews(movieId);
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case TRAILERS_LOADER:
                ArrayList<Trailer> trailers = (ArrayList<Trailer>) data;
                updateUiWithTrailers(trailers);
                break;
            case REVIEWS_LOADER:
                ArrayList<Review> reviews = (ArrayList<Review>) data;
                updateUiWithReviews(reviews);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    private void hideReviews(int id) {
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
        reviewStringTextView.setText(id);
    }

    private void showReviews() {
        trailersRecyclerView.setVisibility(View.VISIBLE);
        reviewStringTextView.setText(reviews);
    }

    private void hideTrailers(int id) {
        trailersRecyclerView.setVisibility(View.INVISIBLE);
        trailerStringTextView.setText(id);
    }

    private void showTrailers() {
        trailersRecyclerView.setVisibility(View.VISIBLE);
        trailerStringTextView.setText(trailers);
    }

}