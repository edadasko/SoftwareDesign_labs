package com.example.battleship.model;

import java.security.InvalidParameterException;

public class Ship {
    private int MinSize = 1;
    private int MaxSize = 4;

    private ShipOrientation orientation;
    private int size;

    public Ship(ShipOrientation orientation, int size) {
        if (size < MinSize || size > MaxSize) {
            throw new InvalidParameterException();
        }

        this.size = size;
        this.orientation = orientation;
    }

    public ShipOrientation getOrientation() {
        return orientation;
    }

    public int getSize() {
        return size;
    }
}
