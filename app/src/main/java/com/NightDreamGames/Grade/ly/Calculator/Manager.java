package com.NightDreamGames.Grade.ly.Calculator;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;
import com.NightDreamGames.Grade.ly.Misc.Preferences;
import com.NightDreamGames.Grade.ly.Misc.Serialization;

import java.util.ArrayList;

public class Manager {
    public static int totalGrades;
    public static ArrayList<Year> years;
    public static ArrayList<Subject> termTemplate;
    public static int currentYear = 0;
    public static int currentTerm = 0;

    public static void init() {
        readPreferences();

        years = new ArrayList<>();
        years.add(new Year());

        termTemplate = new ArrayList<>();
    }

    public static void readPreferences() {
        interpretPreferences();

        currentTerm = Integer.parseInt(Preferences.getPreference("current_term", "0"));
    }

    public static void interpretPreferences() {
        if (!Preferences.getPreference("total_grades", "60").equals("-1"))
            totalGrades = Integer.parseInt(Preferences.getPreference("total_grades", "60"));
        else
            totalGrades = Integer.parseInt(Preferences.getPreference("custom_grade", "60"));
    }

    public static void calculate() {
        if (years == null)
            return;
        for (Year y : years) {
            y.calculate();
        }
    }

    public static void clear() {
        for (Year y : years) {
            for (Term p : y.terms) {
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

    public static Term getCurrentTerm() {
        if (currentTerm == -1) {
            Term p = new Term();

            for (int i = 0; i < getCurrentYear().terms.size(); i++) {
                for (int j = 0; j < getCurrentYear().terms.get(i).subjects.size(); j++) {
                    String name;
                    if (Preferences.getPreference("term", "term_trimester").equals("term_trimester"))
                        name = MainActivity.sApplication.getString(MainActivity.sApplication.getResources().getIdentifier("trimester_" + (i + 1), "string", MainActivity.sApplication.getPackageName()));
                    else
                        name = MainActivity.sApplication.getString(MainActivity.sApplication.getResources().getIdentifier("semester_" + (i + 1), "string", MainActivity.sApplication.getPackageName()));

                    if (getCurrentYear().terms.get(i).subjects.get(j).result != -1)
                        p.subjects.get(j).addTest(new Test(getCurrentYear().terms.get(i).subjects.get(j).result, totalGrades, name));
                }
            }

            p.calculate();
            return p;
        }

        return getCurrentYear().terms.get(currentTerm);
    }

    public static void sortAll() {
        for (Year y : years) {
            for (Term p : y.terms) {
                p.sort();
                for (Subject s : p.subjects) {
                    s.sort();
                }
            }
        }

        Calculator.sort1(termTemplate, "sort_mode3");

        Serialization.Serialize();
    }
}
