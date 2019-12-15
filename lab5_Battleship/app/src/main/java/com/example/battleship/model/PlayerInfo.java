package com.example.battleship.model;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    public String name;

    public PlayerInfo(String name) {
        this.name = name;
    }
}
