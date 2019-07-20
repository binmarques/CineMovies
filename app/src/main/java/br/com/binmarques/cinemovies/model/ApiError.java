package br.com.binmarques.cinemovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class ApiError {

    @Expose
    @SerializedName("status_code")
    private int statusCode;

    @Expose
    @SerializedName("status_message")
    private String statusMessage;

    private boolean success;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
