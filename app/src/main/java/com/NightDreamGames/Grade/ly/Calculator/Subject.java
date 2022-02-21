package com.NightDreamGames.Grade.ly.Calculator;

import com.NightDreamGames.Grade.ly.Misc.Serialization;

import java.util.ArrayList;

public class Subject {
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
            a += t.grade1;
            b += t.grade2;
        }

        double o = a * (Manager.totalGrades / b) + bonus;

        if (o < 0)
            result = 0;
        else {
            result = Calculator.round(o);
        }
    }

    public void addTest(double grade, double total, String name) {
        tests.add(new Test(grade, total, name));
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

    public void editTest(int position, double grade1, double grade2, String name) {
        tests.get(position).grade1 = grade1;
        tests.get(position).grade2 = grade2;
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

    public ArrayList<String> getGrades() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < tests.size(); i++) {
            a.add(Calculator.format(tests.get(i).grade1) + "/" + Calculator.format(tests.get(i).grade2));
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

    public void sort() {
        Calculator.sort2(tests);
    }
}
