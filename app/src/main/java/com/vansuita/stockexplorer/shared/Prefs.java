package com.vansuita.stockexplorer.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.vansuita.stockexplorer.bean.User;

/**
 * Created by jrvansuita on 29/10/17.
 */

public class Prefs {

    private SharedPreferences prefs;
    private static final String PREFS = "PREFS";
    private static final String USER = "USER";
    public Prefs(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static Prefs get(Context context) {
        return new Prefs(context);
    }


    public Prefs setUser(String pUser) {

        User parsed = new Gson().fromJson(pUser, User.class);
        prefs.edit().putString(USER, pUser).apply();

        return this;
    }

    public User getUser() {
        String user = prefs.getString(USER, "");
        if (user != null && !user.isEmpty()) {
            return new Gson().fromJson(user, User.class);
        } else {
            return null;
        }
    }




}
