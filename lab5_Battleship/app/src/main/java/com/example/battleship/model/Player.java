package com.example.battleship.model;

import java.io.Serializable;

public class Player implements Serializable {
    private Grid grid;

    public Player() {
        grid = new Grid();
    }

    public PlayerMoveStatus AttackGrid(Grid enemyGrid, Position position) {
        switch (enemyGrid.getCell(position)) {
            case Filled:
                enemyGrid.setCell(CellStatus.Attacked, position);
                return PlayerMoveStatus.Attacked;

            case Empty:
                enemyGrid.setCell(CellStatus.Missed, position);
                return PlayerMoveStatus.Missed;

            default:
                return PlayerMoveStatus.TryAgain;
        }
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
