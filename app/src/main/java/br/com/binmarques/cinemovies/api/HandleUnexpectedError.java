package br.com.binmarques.cinemovies.api;

import java.lang.annotation.Annotation;

import br.com.binmarques.cinemovies.model.ApiError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class HandleUnexpectedError {

    public static final int RESOURCE_NOT_FOUND = 34;

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter =
                ServiceGenerator.getConverter(ApiError.class, new Annotation[0]);

        ApiError error = null;

        try {
            ResponseBody errorBody = response.errorBody();

            if (errorBody != null) {
                error = converter.convert(errorBody);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return error;
    }

}
