package com.example.battleship.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class GameInfo {
    public String player1Email;
    public String player2Email;
    public String player1Grid;
    public String player2Grid;

    public int player1Score;
    public int player2Score;

    public int currentMove;

    public Date date;

    public GameInfo() {
    }

    public GameInfo(String email1, String email2, String grid1, String grid2) {
        this.player1Email = email1;
        this.player2Email = email2;
        this.player1Grid = grid1;
        this.player2Grid = grid2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.currentMove = 1;
        this.date = new Date(System.currentTimeMillis());
    }

}
