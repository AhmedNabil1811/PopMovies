package com.example.android.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FavoriteMoviesContract {

    private FavoriteMoviesContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class FavoriteMoviesEntry {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final  Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI , TABLE_NAME);
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_POSTER_IMAGE_PATH = "imagePath";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
    }
}