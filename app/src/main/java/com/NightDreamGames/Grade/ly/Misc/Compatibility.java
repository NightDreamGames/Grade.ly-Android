package com.NightDreamGames.Grade.ly.Misc;

import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;
import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Term;

public class Compatibility {
    final static int DATA_VERSION = 2;

    public static void init() {
        termCount("");

        Manager.writePreference("data_version", String.valueOf(DATA_VERSION));
    }

    public static void termCount(String newValue) {
        int k = 0;

        if (newValue.isEmpty())
            newValue = Manager.getPreference("term", "term_trimester");

        switch (newValue) {
            case "term_trimester":
                k = 3;
                break;
            case "term_semester":
                k = 2;
                break;
            case "term_year":
                k = 1;
                break;
        }

        while (Manager.getCurrentYear().terms.size() > k)
            Manager.getCurrentYear().terms.remove(Manager.getCurrentYear().terms.size() - 1);

        while (Manager.getCurrentYear().terms.size() < k)
            Manager.getCurrentYear().terms.add(new Term());

        if (Manager.currentTerm >= Manager.getCurrentYear().terms.size())
            Manager.currentTerm = 0;

        Manager.calculate();
    }

    public static void periodPreferences() {
        if (!Boolean.parseBoolean(Manager.getPreference("isFirstRun", "true")))
            if (Integer.parseInt(Manager.getPreference("data_version", "1")) < 2) {
                Manager.writePreference("term", Manager.getPreference("period", "period_trimester"));
                Manager.writePreference("current_term", Manager.getPreference("current_period", "0"));
                Manager.writePreference("sort_mode1", Manager.getPreference("sort_mode", "0"));
                Manager.deletePreference("period");
                Manager.deletePreference("current_period");
                Manager.deletePreference("sort_mode");

                if (PreferenceManager.getDefaultSharedPreferences(MainActivity.sApplication).contains("data")) {
                    Manager.writePreference("data", Manager.getPreference("data", "")
                            .replaceAll("period", "term")
                            .replaceAll("Period", "Term")
                            .replaceAll("mark", "grade")
                            .replaceAll("Mark", "Grade"));
                    Manager.writePreference("default_data", Manager.getPreference("default_data", "")
                            .replaceAll("period", "term")
                            .replaceAll("Period", "Term")
                            .replaceAll("mark", "grade")
                            .replaceAll("Mark", "Grade"));
                }
            }
    }
}
