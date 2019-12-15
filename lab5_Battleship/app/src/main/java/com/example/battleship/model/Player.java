package com.example.battleship.model;

import java.io.Serializable;

public class Player implements Serializable {
    private Grid grid;
    private String name;

    public Player(String name, Grid grid) {
        this.grid = grid;
        this.name = name;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
