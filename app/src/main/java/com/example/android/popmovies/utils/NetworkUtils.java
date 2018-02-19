package com.example.android.popmovies.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class NetworkUtils<E> {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    protected static final String REQUEST_URL = "http://api.themoviedb.org/3/movie";
    protected static final String API_KEY = "api_key";
    protected static final String API_KEY_PARAM = "api_key";


    public abstract URL creatUrl(String path);

    public abstract E extractDataFromJson(String jsonResponse);

    public static String getResponseFromHttpUrl(URL url) {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(1500);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code" + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving data from JSON", e);
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error in closing input stream", e);
                }
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }

        }
        return builder.toString();
    }
}
