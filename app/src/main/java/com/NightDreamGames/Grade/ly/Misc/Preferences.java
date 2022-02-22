package com.NightDreamGames.Grade.ly.Misc;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;

public class Preferences {

    public static String getPreference(String key, String fallback) {
        return String.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).getString(key, fallback));
    }

    public static void setPreference(String key, String data) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static void deletePreference(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).edit().remove(key);
        editor.apply();
    }

    public static boolean existsPreference(String key) {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).contains(key);
    }
}