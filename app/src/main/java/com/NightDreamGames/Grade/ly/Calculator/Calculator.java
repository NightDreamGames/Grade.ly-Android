package com.NightDreamGames.Grade.ly.Calculator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Calculator {
    @SuppressWarnings("ComparatorCombinators")
    public static void sort1(ArrayList<Subject> data, String sortMode) {
        if (data.size() >= 2) {
            switch (Integer.parseInt(Manager.getPreference(sortMode, "0"))) {
                case 0:
                    Collections.sort(data, (o1, o2) -> Normalizer.normalize(o1.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").compareTo(Normalizer.normalize(o2.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")));
                    break;
                case 1:
                    Collections.sort(data, (o1, o2) -> Double.compare(o2.result, o1.result));
                    break;
            }
        }
    }

    @SuppressWarnings("ComparatorCombinators")
    public static void sort2(ArrayList<Test> data) {
        if (data.size() >= 2) {
            switch (Integer.parseInt(Manager.getPreference("sort_mode2", "0"))) {
                case 0:
                    Collections.sort(data, (o1, o2) -> Normalizer.normalize(o1.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").compareTo(Normalizer.normalize(o2.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")));
                    break;
                case 1:
                    Collections.sort(data, (o1, o2) -> Double.compare(o2.grade1 / o2.grade2, o1.grade1 / o1.grade2));
                    break;
            }
        }
    }

    public static double calculate(ArrayList<Double> results, ArrayList<Double> coefficients) {
        if (results.isEmpty()) {
            return -1;
        }

        double a = 0;
        double b = 0;

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i) != -1) {
                a += results.get(i) * coefficients.get(i);
                b += coefficients.get(i);
            }
        }

        if (b == 0)
            return -1;
        else
            return round(a / b);
    }

    public static double round(double n) {
        String rounding_mode = Manager.getPreference("rounding_mode", "rounding_up");
        int round_to = Integer.parseInt(Manager.getPreference("round_to", "1"));

        switch (rounding_mode) {
            case "rounding_up":
                return Math.ceil(n * round_to) / round_to;
            case "rounding_down":
                return Math.floor(n * round_to) / round_to;
            case "rounding_half_up":
                double i = Math.floor(n * round_to);
                double f = n - i;
                return (f < 0.5 ? i : i + 1D) / round_to;
            case "rounding_half_down":
                double i1 = Math.floor(n * round_to);
                double f1 = n - i1;
                return (f1 <= 0.5 ? i1 : i1 + 1D) / round_to;
            default:
                return n;
        }
    }

    public static String format(double n) {
        String a;
        if (n == (long) n) {
            a = String.format(Locale.getDefault(), "%d", (long) n);
        } else {
            a = String.format(Locale.getDefault(), "%s", n);
        }
        return (n < 10) ? 0 + a : a;
    }

}
