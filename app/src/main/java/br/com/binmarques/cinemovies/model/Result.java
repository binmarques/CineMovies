package br.com.binmarques.cinemovies.model;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

/**
 * Created By Daniel Marques on 06/08/2018
 */

public class Result {

    private long id;

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/w185";

    private static final String BASE_BACKDROP_URL = "https://image.tmdb.org/t/p/w500";

    @Expose
    @SerializedName("vote_average")
    private double voteAverage;

    private String title;

    @Expose
    @SerializedName("poster_path")
    private String posterPath;

    @Expose
    @SerializedName("backdrop_path")
    private String backdropPath;

    private String overview;

    @Expose
    @SerializedName("release_date")
    private String releaseDate;

    @Expose
    @SerializedName("runtime")
    private int runTime;

    private String homepage;

    private LinkedList<Genre> genres;

    public Result() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return BASE_POSTER_URL + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return !TextUtils.isEmpty(backdropPath) ? BASE_BACKDROP_URL + backdropPath : "";
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return !TextUtils.isEmpty(releaseDate) ? releaseDate.substring(0, 4) : "";
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    private int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public LinkedList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(LinkedList<Genre> genres) {
        this.genres = genres;
    }

    public String getRunTimeInHoursAndMinutes() {
        int hours = (getRunTime() / 60);
        int minutes = (getRunTime() % 60);
        return (hours + "h " + minutes);
    }

    public String getPosterAsBackdrop() {
        return BASE_BACKDROP_URL + posterPath;
    }

    @NonNull
    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", voteAverage=" + voteAverage +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", runTime=" + runTime +
                ", homepage='" + homepage + '\'' +
                ", genres=" + genres +
                '}';
    }
}
