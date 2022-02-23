package com.NightDreamGames.Grade.ly.Misc;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Term;

public class Compatibility {
    final static int DATA_VERSION = 2;

    public static void init() {
        termCount("");

        Preferences.setPreference("data_version", String.valueOf(DATA_VERSION));
    }

    public static void termCount(String newValue) {
        int k = 0;

        if (newValue.isEmpty())
            newValue = Preferences.getPreference("term", "term_trimester");

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

        if (Manager.currentTerm >= Manager.getCurrentYear().terms.size()) {
            Manager.currentTerm = 0;
            Preferences.setPreference("current_term", String.valueOf(Manager.currentTerm));
        }
        Manager.calculate();
        Serialization.Serialize();
    }

    public static void periodPreferences() {
        if (!Boolean.parseBoolean(Preferences.getPreference("isFirstRun", "true"))) {
            if (Integer.parseInt(Preferences.getPreference("data_version", "1")) < 2) {
                Preferences.setPreference("term", Preferences.getPreference("period", "term_trimester").replace("period", "term"));
                Preferences.setPreference("current_term", Preferences.getPreference("current_period", "0"));
                Preferences.setPreference("sort_mode1", Preferences.getPreference("sort_mode", "0"));
                Preferences.deletePreference("period");
                Preferences.deletePreference("current_period");
                Preferences.deletePreference("sort_mode");

                //TODO test if this works
                if (Preferences.existsPreference("data")) {
                    Preferences.setPreference("data", Preferences.getPreference("data", "")
                            .replaceAll("period", "term")
                            .replaceAll("mark", "grade"));
                    Preferences.setPreference("default_data", Preferences.getPreference("default_data", "")
                            .replaceAll("period", "term")
                            .replaceAll("mark", "grade"));
                }
            }
        }
    }
}
