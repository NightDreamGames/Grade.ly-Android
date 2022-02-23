package com.NightDreamGames.Grade.ly.Misc;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Subject;
import com.NightDreamGames.Grade.ly.Calculator.Year;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class Serialization {
    private static final Gson gson = new Gson();

    public static void Serialize() {
        Preferences.setPreference("data", gson.toJson(Manager.years));
        Preferences.setPreference("default_data", gson.toJson(Manager.termTemplate));
    }

    public static void Deserialize() {
        if (Preferences.existsPreference("data")) {
            Manager.years = gson.fromJson(Preferences.getPreference("data", ""), new TypeToken<ArrayList<Year>>() {
            }.getType());

            Manager.termTemplate = gson.fromJson(Preferences.getPreference("default_data", ""), new TypeToken<ArrayList<Subject>>() {
            }.getType());


        }
    }
}
