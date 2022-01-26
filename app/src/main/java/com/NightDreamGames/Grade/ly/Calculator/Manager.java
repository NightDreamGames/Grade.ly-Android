package com.NightDreamGames.Grade.ly.Calculator;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;
import com.NightDreamGames.Grade.ly.Misc.Serialization;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class Manager {
    public static int totalMarks;
    public static ArrayList<Year> years;
    public static ArrayList<Subject> periodTemplate = new ArrayList<>();
    public static int currentYear = 0;
    public static int currentPeriod = 0;

    public static void init() {
        readPreferences();

        Serialization.Deserialize();

        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
            years.add(new Year());
        }
        if (periodTemplate == null || periodTemplate.isEmpty()) {
            periodTemplate = new ArrayList<>();
        }

        for (Year y : years) {
            y.calculate();
        }
    }

    public static void readPreferences() {
        if (!getPreference("total_marks", "60").equals("-1"))
            totalMarks = Integer.parseInt(getPreference("total_marks", "60"));
        else
            totalMarks = Integer.parseInt(getPreference("custom_mark", "60"));

        currentPeriod = Integer.parseInt(getPreference("current_period", "0"));
    }

    public static String getPreference(String key, String fallback) {
        return String.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).getString(key, fallback));
    }

    public static void writePreference(String key, String data) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static void deletePreference(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).edit().remove(key);
        editor.apply();
    }

    public static void calculate() {
        if (years == null)
            return;
        for (Year y : years) {
            y.calculate();
        }
    }

    public static String format(double d) {
        String a;
        if (d == (long) d) {
            a = String.format(Locale.getDefault(), "%d", (long) d);
        } else {
            a = String.format(Locale.getDefault(), "%s", d);
        }
        return (d < 10) ? 0 + a : a;
    }

    public static void clear() {
        for (Year y : years) {
            for (Period p : y.periods) {
                for (Subject s : p.subjects) {
                    for (int i = 0; i < s.tests.size(); ) {
                        s.removeTest(i);
                    }
                    s.bonus = 0;
                }
            }
        }
    }

    public static Year getCurrentYear() {
        return years.get(currentYear);
    }

    public static Period getCurrentPeriod() {
        if (currentPeriod == -1) {
            Period p = new Period();

            for (Period per : getCurrentYear().periods) {
                for (int j = 0; j < per.subjects.size(); j++) {
                    for (int k = 0; k < per.subjects.get(j).tests.size(); k++) {
                        p.subjects.get(j).addTest(per.subjects.get(j).tests.get(k));
                    }
                }
            }

            p.calculate();
            return p;
        }

        return years.get(currentYear).periods.get(currentPeriod);
    }

    @SuppressWarnings("ComparatorCombinators")
    public static void sortAll() {
        for (Year y : years) {
            for (Period p : y.periods) {
                p.sort();
                for (Subject s : p.subjects) {
                    s.sort();
                }
            }
        }

        switch (Integer.parseInt(Manager.getPreference("sort_mode", "0"))) {
            case 0:
                periodTemplate.sort((o1, o2) -> Normalizer.normalize(o1.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").compareTo(Normalizer.normalize(o2.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")));
                break;
            case 1:
                periodTemplate.sort((o1, o2) -> Double.compare(o2.coefficient, o1.coefficient));
                break;
        }

        Serialization.Serialize();
    }
}
