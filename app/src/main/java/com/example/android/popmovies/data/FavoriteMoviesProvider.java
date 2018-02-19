package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

public class FavoriteMoviesProvider extends ContentProvider {
    private static final String TAG = FavoriteMoviesProvider.class.getSimpleName();

    private FavoriteMoviesDbHelper helper;

    private static final int FAVORITE_MOVIES = 1;
    private static final int FAVORITE_MOVIE_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesEntry.TABLE_NAME, FAVORITE_MOVIES);

        sUriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesEntry.TABLE_NAME + "/#", FAVORITE_MOVIE_ID);
    }


    @Override
    public boolean onCreate() {
        helper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {

            case FAVORITE_MOVIES:
                cursor = db.query(FavoriteMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case FAVORITE_MOVIE_ID:
                selection = FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(FavoriteMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(FavoriteMoviesEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rawsDeleted;
        int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case FAVORITE_MOVIES:
                rawsDeleted = db.delete(FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE_ID:
                selection = FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rawsDeleted = db.delete(FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rawsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rawsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
