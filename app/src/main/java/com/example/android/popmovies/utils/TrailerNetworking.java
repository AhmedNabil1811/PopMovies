package com.example.android.popmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TrailerNetworking extends NetworkUtils<ArrayList<Trailer>> {

    private static final String TAG = TrailerNetworking.class.getSimpleName();

    @Override
    public URL creatUrl(String path) {
        String id = path;
        Uri uri = Uri.parse(NetworkUtils.REQUEST_URL).buildUpon()
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, NetworkUtils.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "invalid Trailer URL");
        }

        return url;
    }

    @Override
    public ArrayList<Trailer> extractDataFromJson(String jsonResponse) {
        if (jsonResponse == null || TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(jsonResponse);
            JSONArray result = object.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject currentVideo = result.getJSONObject(i);
                String type = currentVideo.getString("type");
                if (type.equals("Trailer")) {
                    String key = currentVideo.getString("key");
                    Trailer trailer = new Trailer(key);
                    trailers.add(trailer);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }
}
