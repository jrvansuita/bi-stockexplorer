package com.vansuita.stockexplorer.retro;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jrvansuita on 16/10/17.
 */

public abstract class Retro<T> implements Callback<T> {

    public abstract void onResponse(T result);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onResponse(response.body());
        } else {
            onFailure(call, new Throwable(response.message()));
        }
    }

    public String getSearchTerm(Call<T> call) {
        List<String> list = new ArrayList(call.request().url().pathSegments());
        String[] search = list.get(list.size() - 1).split("=");
        return search[search.length - 1];
    }


    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }

    public interface IOnRequestError {

        void onRequestError(String search, Throwable error);
    }


}
