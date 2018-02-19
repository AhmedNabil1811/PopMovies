package com.example.android.popmovies.models;

public class Review {
    private String authorName;
    private String content;

    public Review(String authorName, String content) {
        this.authorName = authorName;
        this.content = content;
    }

    public String getAuthorName() {

        return authorName;
    }

    public String getContent() {

        return content;
    }
}
