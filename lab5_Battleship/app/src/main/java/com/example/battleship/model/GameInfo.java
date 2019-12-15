package com.example.battleship.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GameInfo {
    public String player1Id;
    public String player2Id;
    public String player1Grid;
    public String player2Grid;

    public GameInfo() {
    }

    public GameInfo(String id1, String id2, String grid1, String grid2) {
        this.player1Id = id1;
        this.player2Id = id2;
        this.player1Grid = grid1;
        this.player2Grid = grid2;
    }

}
