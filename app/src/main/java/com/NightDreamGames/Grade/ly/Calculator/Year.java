package com.NightDreamGames.Grade.ly.Calculator;

import java.io.Serializable;
import java.util.ArrayList;

public class Year implements Serializable {
    public final ArrayList<Term> terms = new ArrayList<>();

    public double result;

    public Year() {
        int k = 0;
        switch (Manager.getPreference("term", "term_trimester")) {
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

        Manager.currentTerm = 0;

        calculate();
    }

    public void calculate() {
        ArrayList<Double> results = new ArrayList<>();
        ArrayList<Double> coefficients = new ArrayList<>();

        for (Term t : terms) {
            results.add(t.result);
            coefficients.add(1.0);
        }

        Calculator.calculate(results, coefficients);
    }
}
