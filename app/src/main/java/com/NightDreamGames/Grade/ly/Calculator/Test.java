package com.NightDreamGames.Grade.ly.Calculator;

import java.io.Serializable;

public class Test implements Serializable {
    public double grade1;
    public double grade2;
    public String name;

    public Test(double grade1, double grade2, String name) {
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.name = name;
    }
}
