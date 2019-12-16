package com.example.battleship.model;

import java.util.Date;

public class GameStats {
    public String user1Email;
    public String user2Email;
    public long score1;
    public long score2;
    public Date date;

    public GameStats(String email1, String email2, long score1, long score2, Date date) {
        this.user1Email = email1;
        this.user2Email = email2;
        this.score1 = score1;
        this.score2 = score2;
        this.date = date;
    }
}
