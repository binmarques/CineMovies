package br.com.binmarques.cinemovies.api;

import java.lang.annotation.Annotation;

import br.com.binmarques.cinemovies.BuildConfig;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created By Daniel Marques on 05/08/2018
 */

public class ServiceGenerator {

    private static Retrofit.Builder sBuilder =
            new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit sRetrofit = sBuilder.build();

    public static <T> T createService(Class<T> serviceClass) {
        return sRetrofit.create(serviceClass);
    }

    public static <T> Converter<ResponseBody, T> getConverter(Class<T> clazz, Annotation[] a) {
        return sRetrofit.responseBodyConverter(clazz, a);
    }

}
