package com.NightDreamGames.Grade.ly.Calculator;

import java.io.Serializable;
import java.util.ArrayList;

public class Year implements Serializable {
    public final ArrayList<Period> periods = new ArrayList<>();

    public double result;

    public Year() {
        int k = 0;
        switch (Manager.getPreference("period", "period_trimester")) {
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
        for (int i = 0; i < k; i++) {
            periods.add(new Period());
        }

        Manager.currentPeriod = 0;

        calculate();
    }

    public void calculate() {
        if (periods.isEmpty()) {
            result = -1;
            return;
        }

        double a = 0;
        double b = 0;
        for (Period s : periods) {
            s.calculate();

            if (s.result != -1) {
                a += s.result;
                b++;
            }
        }
        if (b == 0)
            result = -1;
        else {
            double o = a / b;

            String rounding_mode = Manager.getPreference("rounding_mode", "rounding_up");
            int round_to = Integer.parseInt(Manager.getPreference("round_to", "1"));

            switch (rounding_mode) {
                case "rounding_up":
                    result = Math.ceil(o * round_to) / round_to;
                    break;
                case "rounding_down":
                    result = Math.floor(o * round_to) / round_to;
                    break;
                case "rounding_half_up":
                    double i = Math.floor(o * round_to);
                    double f = o - i;
                    result = (f < 0.5 ? i : i + 1D) / round_to;
                    break;
                case "rounding_half_down":
                    double i1 = Math.floor(o * round_to);
                    double f1 = o - i1;
                    result = (f1 <= 0.5 ? i1 : i1 + 1D) / round_to;
                    break;
            }
        }
    }
}
