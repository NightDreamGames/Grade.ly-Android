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
        try {
            Manager.writePreference("data", gson.toJson(Manager.years));
            Manager.writePreference("default_data", gson.toJson(Manager.periodTemplate));
        } catch (Exception e) {
            Serialize();
        }
    }

    public static void Deserialize() {
        try {
            Manager.years = gson.fromJson(Manager.getPreference("data", ""), new TypeToken<ArrayList<Year>>() {
            }.getType());
            Manager.periodTemplate = gson.fromJson(Manager.getPreference("default_data", ""), new TypeToken<ArrayList<Subject>>() {
            }.getType());
        } catch (Exception e) {
            Manager.deletePreference("data");
            Manager.deletePreference("default_data");
            Deserialize();
        }
    }
}
