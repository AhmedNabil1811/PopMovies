package com.example.android.popmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String posterImagePath;
    private String releaseDate;
    private String rating;
    private String overview;


    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterImagePath = in.readString();
        releaseDate = in.readString();
        rating = in.readString();
        overview = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRating() {
        return rating;
    }


    public String getOverview() {
        return overview;
    }

    public Movie(String id, String title, String posterImagePath, String releaseDate, String rating, String overview) {
        this.id = id;
        this.title = title;
        this.posterImagePath = posterImagePath;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.overview = overview;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", posterImagePath='" + posterImagePath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", rating='" + rating + '\'' +
                ", overview='" + overview + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterImagePath);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(overview);
    }

}