package com.example.android.popmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.adapters.FavoriteMoviesAdapter;
import com.example.android.popmovies.adapters.MovieAdapter;
import com.example.android.popmovies.data.FavoriteMoviesContract;
import com.example.android.popmovies.models.Movie;
import com.example.android.popmovies.utils.MovieNetworking;
import com.example.android.popmovies.utils.NetworkUtils;
import com.example.android.popmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.id.message;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;
import static com.example.android.popmovies.activities.MovieDetailsActivity.MOVIE_EXTRA_KEY;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks, FavoriteMoviesAdapter.FavoriteMovieAdapterOnClickHandler {

    private static final int MOVIES_LOADER = 100;
    private static final int FAVORITE_MOVIES_LOADER = 200;
    private static final int FAVORITE_LIST_STATE = 1;
    private static final int MOVIE_LIST_STATE = 2;
    private static final String QUERY_URL_EXTRA = "query";
    private static final String FAVORITE_LIST_EXTRA = "favorite_list";
    private static final String LIST_STATE_KEY = "listState";

    private Parcelable listState;
    private ArrayList<Movie> mFavoriteMovies;
    private static int currentListState;

    private RecyclerView recyclerView;
    GridLayoutManager manager;
    private TextView errorMessageTextView;
    private MovieAdapter adapter;
    private ProgressBar loadingIndicator;
    private FavoriteMoviesAdapter favoritesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(FAVORITE_LIST_EXTRA, currentListState);

        listState = manager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY , listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        manager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(manager);

        recyclerView.setHasFixedSize(true);

        favoritesAdapter = new FavoriteMoviesAdapter(this);
        adapter = new MovieAdapter(this);
        recyclerView.setAdapter(adapter);


        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(FAVORITE_MOVIES_LOADER);
        if (loader == null) {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        } else {
            getSupportLoaderManager().restartLoader(FAVORITE_MOVIES_LOADER, null, this);
        }

        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt(FAVORITE_LIST_EXTRA)) {
                case MOVIE_LIST_STATE:
                    getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
                    currentListState = MOVIE_LIST_STATE;
                    break;
                case FAVORITE_LIST_STATE:
                    recyclerView.setAdapter(favoritesAdapter);
                    currentListState = FAVORITE_LIST_STATE;
                    break;
            }

        } else {
            getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
            currentListState = MOVIE_LIST_STATE;
        }
    }

    @Override
    public void onMovieItemClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.setExtrasClassLoader(Movie.class.getClassLoader());
        intent.putExtra(MOVIE_EXTRA_KEY, movie);
        intent.putExtra(MovieDetailsActivity.FAVORITE_MOVIE_EXTRA, isAfavoriteMovie(movie));
        startActivity(intent);
    }

    private boolean isAfavoriteMovie(Movie movie) {
        if (mFavoriteMovies == null || mFavoriteMovies.isEmpty()) {
            return false;
        }
        for (int i = 0; i < mFavoriteMovies.size(); i++) {
            if (mFavoriteMovies.get(i).getId().equals(movie.getId())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        adapter.setmMovieData(null);
        Bundle queryBundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                queryBundle.putString(QUERY_URL_EXTRA, MovieNetworking.DEFAULT_SORT_ORDER);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER, queryBundle, this);
                recyclerView.setAdapter(adapter);
                currentListState = MOVIE_LIST_STATE;
                break;
            case R.id.action_sort_high_rated:
                queryBundle.putString(QUERY_URL_EXTRA, MovieNetworking.SORT_ORDER);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER, queryBundle, this);
                recyclerView.setAdapter(adapter);
                currentListState = MOVIE_LIST_STATE;
                break;
            case R.id.action_sort_favorite_list:
                favoritesAdapter.swapCursor(null);
                getSupportLoaderManager().restartLoader(FAVORITE_MOVIES_LOADER, null, this);
                recyclerView.setAdapter(favoritesAdapter);
                currentListState = FAVORITE_LIST_STATE;
                break;
        }
        return true;
    }

    private void showErrorMessage(int id) {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setText(id);
    }

    private void showMovieList() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MOVIES_LOADER:
                return new AsyncTaskLoader(this) {
                    ArrayList<Movie> mMovies;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        loadingIndicator.setVisibility(View.VISIBLE);
                        if (mMovies != null) {
                            deliverResult(mMovies);
                            loadingIndicator.setVisibility(View.INVISIBLE);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public void deliverResult(Object data) {
                        mMovies = (ArrayList<Movie>) data;
                        super.deliverResult(data);
                    }

                    @Override
                    public Object loadInBackground() {
                        MovieNetworking movieNetworking = new MovieNetworking();

                        String queryUrl = null;
                        if (args != null) {
                            queryUrl = args.getString(QUERY_URL_EXTRA);
                        }

                        URL url = movieNetworking.creatUrl(queryUrl);

                        String jasonResponse = NetworkUtils.getResponseFromHttpUrl(url);

                        ArrayList<Movie> movies = movieNetworking.extractDataFromJson(jasonResponse);

                        return movies;
                    }
                };
            case FAVORITE_MOVIES_LOADER:
                return new CursorLoader(this,
                        FavoriteMoviesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        switch (loader.getId()) {
            case MOVIES_LOADER:
                if (data == null) {
                    showErrorMessage(R.string.error_message);
                } else {
                    showMovieList();
                    adapter.setmMovieData((ArrayList<Movie>) data);

                    if(listState != null){
                        manager.onRestoreInstanceState(listState);
                    }
                }
                break;
            case FAVORITE_MOVIES_LOADER:
                Cursor cursor = (Cursor) data;
                if (cursor == null || cursor.getCount() < 1) {
                    showErrorMessage(R.string.no_favorite_list);
                } else {
                    showMovieList();
                    favoritesAdapter.swapCursor(cursor);
                    mFavoriteMovies = extractDataFromCursor(cursor);
                }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        favoritesAdapter.swapCursor(null);
    }


    private ArrayList<Movie> extractDataFromCursor(Cursor cursor) {
        ArrayList<Movie> favoriteMovies = new ArrayList<>();

        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() < 1) {
            return null;
        }

        while (cursor.moveToNext()) {
            int currentId = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_ID));
            String currentName = cursor.getString(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_TITLE));
            String currentPosterImagePath = cursor.getString(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH));
            String currentReleaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE));
            float currentRating = cursor.getFloat(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_RATING));
            String currentOverView = cursor.getString(cursor.getColumnIndex(FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW));

            Movie movie = new Movie(String.valueOf(currentId), currentName, currentPosterImagePath, currentReleaseDate, String.valueOf(currentRating), currentOverView);
            favoriteMovies.add(movie);
        }
        return favoriteMovies;
    }

    @Override
    public void onFavoriteMovieItemClick(int cursorPosition) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.setExtrasClassLoader(Movie.class.getClassLoader());
        intent.putExtra(MOVIE_EXTRA_KEY, mFavoriteMovies.get(cursorPosition));
        intent.putExtra(MovieDetailsActivity.FAVORITE_MOVIE_EXTRA, true);
        startActivity(intent);
    }

}