package com.universita.laboratorioium.utils;

import java.util.List;

public class Day {
    List<Teach> a,b,c,d;

    public Day(List<Teach> a, List<Teach> b, List<Teach> c, List<Teach> d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public List<Teach> getA() {
        return a;
    }

    public List<Teach> getB() {
        return b;
    }

    public List<Teach> getC() {
        return c;
    }

    public List<Teach> getD() {
        return d;
    }
}
