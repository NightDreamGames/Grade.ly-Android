package com.NightDreamGames.Grade.ly.Calculator;

import java.io.Serializable;

public class Test implements Serializable {
    public double mark1;
    public double mark2;
    public String name;

    public Test(double mark1, double mark2, String name) {
        this.mark1 = mark1;
        this.mark2 = mark2;
        this.name = name;
    }
}
