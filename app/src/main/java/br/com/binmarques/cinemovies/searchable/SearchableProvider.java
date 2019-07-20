package br.com.binmarques.cinemovies.searchable;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created By Daniel Marques on 31/08/2018
 */

public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "br.com.binmarques.cinemovies.searchable.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
