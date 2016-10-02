package com.example.gaurav.androidpower;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Gaurav on 6/19/2016.
 */
public class Session {

    private SharedPreferences prefs;

    SharedPreferences.Editor editor;

    private String ISSSET;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        editor = prefs.edit();
    }

    public void setusertoken(String userToken) {
        editor.putBoolean(ISSSET, true);
        editor.putString("userToken", userToken);
        editor.commit();
    }

    public String getusertoken() {
        String userToken = prefs.getString("userToken","");
        return userToken;
    }

    public boolean isset(){
        return prefs.getBoolean(ISSSET, false);
    }
}