package com.example.android.popmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.path;

public class MovieNetworking extends NetworkUtils<ArrayList<Movie>> {

    private static final String TAG = MovieNetworking.class.getSimpleName();

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p";
    private static final String IMAGE_SIZE = "w300";

    public static final String DEFAULT_SORT_ORDER = "popular";
    public static final String SORT_ORDER = "top_rated";
    private static final String REGION = "US";
    private static final String LANGUAGE = "en-US";

    private static final String REGION_PARAM = "region";
    private static final String LANGUAGE_PARAM = "language";

    @Override
    public URL creatUrl(String sortOrder) {
        String currentSortOrder;
        if (sortOrder == null) {
            currentSortOrder = DEFAULT_SORT_ORDER;
        } else {
            currentSortOrder = sortOrder;
        }

        Uri uri = Uri.parse(NetworkUtils.REQUEST_URL).buildUpon()
                .appendPath(currentSortOrder)
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, NetworkUtils.API_KEY)
                .appendQueryParameter(REGION_PARAM, REGION)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "invalid URL");
        }

        return url;
    }

    @Override
    public ArrayList<Movie> extractDataFromJson(String jsonResponse) {
        if (jsonResponse == null || TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<Movie>();
        try {
            JSONObject object = new JSONObject(jsonResponse);
            JSONArray result = object.getJSONArray("results");

            for (int i = 0; i < result.length(); i++) {
                JSONObject currentMovie = result.getJSONObject(i);
                int movieId = currentMovie.getInt("id");
                String movieTitle = currentMovie.getString("original_title");
                String posterImagePath = currentMovie.getString("poster_path");
                String movieDate = currentMovie.getString("release_date");
                String movieRate = String.valueOf(currentMovie.getDouble("vote_average"));
                String overview = currentMovie.getString("overview");
                Movie movie = new Movie(String.valueOf(movieId), movieTitle, parseImagePath(posterImagePath), movieDate, movieRate, overview);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private static String parseImagePath(String path) {
        Uri uri = Uri.parse(BASE_IMAGE_URL).buildUpon().appendPath(IMAGE_SIZE).build();
        return uri.toString() + path;
    }
}

