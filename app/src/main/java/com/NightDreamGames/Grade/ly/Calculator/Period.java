package com.NightDreamGames.Grade.ly.Calculator;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

public class Period implements Serializable {
    public final ArrayList<Subject> subjects = new ArrayList<>();

    public double result;

    public Period() {
        if (Manager.periodTemplate != null && !Manager.periodTemplate.isEmpty()) {
            for (Subject s : Manager.periodTemplate) {
                subjects.add(new Subject(s.name, s.coefficient));
            }
        }

        Manager.calculate();
    }

    public void calculate() {
        if (subjects.isEmpty()) {
            result = -1;
            return;
        }

        for (Subject s : subjects) {
            s.calculate();
        }

        double a = 0;
        double b = 0;
        for (Subject s : subjects) {
            if (s.result != -1) {
                a += s.result * s.coefficient;
                b += s.coefficient;
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

    public ArrayList<String> getSubjects() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            a.add(subjects.get(i).name);
        }
        return a;
    }

    public ArrayList<String> getMarks() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            a.add((subjects.get(i).result == -1) ? "-" : Manager.format(subjects.get(i).result));
        }
        return a;
    }

    @SuppressWarnings("ComparatorCombinators")
    public void sort() {
        if (subjects.size() >= 2) {
            switch (Integer.parseInt(Manager.getPreference("sort_mode", "0"))) {
                case 0:
                    Collections.sort(subjects, (o1, o2) -> Normalizer.normalize(o1.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").compareTo(Normalizer.normalize(o2.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")));
                    break;
                case 1:
                    Collections.sort(subjects, (o1, o2) -> Double.compare(o2.result, o1.result));
                    break;
            }
        }
    }
}
