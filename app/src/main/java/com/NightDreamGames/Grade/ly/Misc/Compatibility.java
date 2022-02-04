package com.NightDreamGames.Grade.ly.Misc;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Period;

public class Compatibility {
    public static void init() {
        periodCount("");
    }

    public static void periodCount(String newValue) {
        int k = 0;

        if (newValue.isEmpty())
            newValue = Manager.getPreference("period", "period_trimester");

        switch (newValue) {
            case "period_trimester":
                k = 3;
                break;
            case "period_semester":
                k = 2;
                break;
            case "period_year":
                k = 1;
                break;
        }

        while (Manager.getCurrentYear().periods.size() > k)
            Manager.getCurrentYear().periods.remove(Manager.getCurrentYear().periods.size() - 1);

        while (Manager.getCurrentYear().periods.size() < k)
            Manager.getCurrentYear().periods.add(new Period());

        if (Manager.currentPeriod >= Manager.getCurrentYear().periods.size())
            Manager.currentPeriod = 0;

        Manager.calculate();
    }
}
