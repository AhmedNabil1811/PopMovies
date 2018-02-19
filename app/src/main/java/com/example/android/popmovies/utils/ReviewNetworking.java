package com.example.android.popmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewNetworking extends NetworkUtils<ArrayList<Review>> {

    private static final String TAG = ReviewNetworking.class.getSimpleName();

    @Override
    public URL creatUrl(String path) {
        String id = path;
        Uri uri = Uri.parse(NetworkUtils.REQUEST_URL).buildUpon()
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, NetworkUtils.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "invalid Review URL");
        }

        return url;
    }

    @Override
    public ArrayList<Review> extractDataFromJson(String jsonResponse) {
        if (jsonResponse == null || TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(jsonResponse);
            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentReview = results.getJSONObject(i);
                String authorName = currentReview.getString("author");
                String content = currentReview.getString("content").trim();
                Review review = new Review(authorName, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}