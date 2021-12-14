package com.NightDreamGames.Grade.ly.Calculator;

import com.NightDreamGames.Grade.ly.Misc.Serialization;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

public class Subject implements Serializable {
    public final ArrayList<Test> tests = new ArrayList<>();

    public String name;
    public double coefficient;
    public double result;
    public int bonus;

    public Subject(String name, double coefficient) {
        this.name = name;
        this.coefficient = coefficient;
        Manager.calculate();
    }

    public void calculate() {
        if (tests.isEmpty()) {
            result = -1;
            return;
        }

        double a = 0;
        double b = 0;
        for (Test t : tests) {
            a += t.mark1;
            b += t.mark2;
        }

        double o = a * (Manager.totalMarks / b) + bonus;

        if (o < 0)
            result = 0;
        else {
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

    public void addTest(double mark, double total, String name) {
        tests.add(new Test(mark, total, name));
        Manager.calculate();
        Serialization.Serialize();
    }

    public void addTest(Test test) {
        tests.add(test);
        Manager.calculate();
        Serialization.Serialize();
    }

    public void removeTest(int position) {
        tests.remove(position);
        Manager.calculate();
        Serialization.Serialize();
    }

    public void editTest(int position, double mark1, double mark2, String name) {
        tests.get(position).mark1 = mark1;
        tests.get(position).mark2 = mark2;
        tests.get(position).name = name;
        Manager.calculate();
        Serialization.Serialize();
    }

    public ArrayList<String> getNames() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < tests.size(); i++) {
            a.add(tests.get(i).name);
        }

        return a;
    }

    public ArrayList<String> getMarks() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < tests.size(); i++) {
            a.add(Manager.format(tests.get(i).mark1) + "/" + Manager.format(tests.get(i).mark2));
        }

        return a;
    }

    public void changeBonus(int direction) {
        if (Math.abs(bonus + direction) < 10) {
            bonus += direction;
            Manager.calculate();
            Serialization.Serialize();
        }
    }

    @SuppressWarnings("ComparatorCombinators")
    public void sort() {
        if (tests.size() >= 2) {
            switch (Integer.parseInt(Manager.getPreference("sort_mode2", "0"))) {
                case 0:
                    Collections.sort(tests, (o1, o2) -> Normalizer.normalize(o1.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").compareTo(Normalizer.normalize(o2.name.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")));
                    break;
                case 1:
                    Collections.sort(tests, (o1, o2) -> Double.compare(o2.mark1 / o2.mark2, o1.mark1 / o1.mark2));
                    break;
            }
        }
    }
}
