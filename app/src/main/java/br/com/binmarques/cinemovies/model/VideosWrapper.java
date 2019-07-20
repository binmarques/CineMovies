package br.com.binmarques.cinemovies.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created By Daniel Marques on 04/11/2018
 */

public class VideosWrapper {

    private long id;

    private List<Video> results;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public String toString() {
        return "VideosWrapper{" +
                "id=" + id +
                ", results=" + results +
                '}';
    }
}
