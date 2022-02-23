package com.NightDreamGames.Grade.ly.Calculator;

import com.NightDreamGames.Grade.ly.Misc.Preferences;

import java.util.ArrayList;

public class Year {
    public final ArrayList<Term> terms = new ArrayList<>();

    public double result;

    public Year() {
        int k = 0;
        switch (Preferences.getPreference("term", "term_trimester")) {
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
        for (int i = 0; i < k; i++) {
            terms.add(new Term());
        }

        calculate();
    }

    public void calculate() {
        ArrayList<Double> results = new ArrayList<>();
        ArrayList<Double> coefficients = new ArrayList<>();

        for (Term t : terms) {
            t.calculate();

            results.add(t.result);
            coefficients.add(1.0);
        }

        result = Calculator.calculate(results, coefficients);
    }
}
