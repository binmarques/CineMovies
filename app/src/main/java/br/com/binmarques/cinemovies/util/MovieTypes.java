package br.com.binmarques.cinemovies.util;

/**
 * Created By Daniel Marques on 07/08/2018
 */

public enum MovieTypes {

    THEATERS("Agora nos cinemas"),

    POPULAR("Mais populares"),

    TOP_RATED("Mais votados"),

    UPCOMING("Os mais aguardados");

    private String description;

    MovieTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
