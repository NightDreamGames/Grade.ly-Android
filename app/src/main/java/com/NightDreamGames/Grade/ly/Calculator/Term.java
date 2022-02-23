package com.NightDreamGames.Grade.ly.Calculator;

import java.util.ArrayList;

public class Term {
    public ArrayList<Subject> subjects = new ArrayList<>();

    public double result;

    public Term() {
        if (Manager.termTemplate != null && !Manager.termTemplate.isEmpty()) {
            for (Subject s : Manager.termTemplate) {
                subjects.add(new Subject(s.name, s.coefficient));
            }
        }

        Manager.calculate();
    }

    public void calculate() {
        ArrayList<Double> results = new ArrayList<>();
        ArrayList<Double> coefficients = new ArrayList<>();

        for (Subject s : subjects) {
            s.calculate();

            results.add(s.result);
            coefficients.add(s.coefficient);
        }

        result = Calculator.calculate(results, coefficients);
    }

    public ArrayList<String> getSubjects() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            a.add(subjects.get(i).name);
        }
        return a;
    }

    public ArrayList<String> getGrades() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            a.add((subjects.get(i).result == -1) ? "-" : Calculator.format(subjects.get(i).result));
        }
        return a;
    }

    public void sort() {
        Calculator.sort1(subjects, "sort_mode1");
    }
}
