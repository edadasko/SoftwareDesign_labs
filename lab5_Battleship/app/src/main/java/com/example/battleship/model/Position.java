package com.example.battleship.model;

import java.io.Serializable;
import java.security.InvalidParameterException;

public class Position implements Serializable {
    public static int MaxX = 9;
    public static int MaxY = 9;

    public static int MinX = 0;
    public static int MinY = 0;

    private int x;
    private int y;

    public Position(int x, int y) {
        if (x > MaxX || x < MinX || y > MaxY || y < MinY) {
            throw new InvalidParameterException();
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
