package br.com.binmarques.cinemovies.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created By Daniel Marques on 04/11/2018
 */

public class Video {

    public static final String SITE_YOUTUBE = "YouTube";

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    public static final String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/";

    private String id;

    @Expose
    @SerializedName("iso_639_1")
    private String iso;

    private String key;

    private String name;

    private String site;

    private int size;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", iso='" + iso + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }

}
