package com.example.battleship.model;

import java.io.Serializable;

public class Grid implements Serializable {
    public static final int Height = Position.MaxX + 1;
    public static final int Width = Position.MaxY + 1;

    private CellStatus[][] cells;

    public Grid() {
        cells = new CellStatus[Height][Width];

        for (int i = 0; i < Height; i++)
            for (int j = 0; j < Width; j++)
                cells[i][j] = CellStatus.Empty;
    }

    public CellStatus[][] getCells() {
        return cells;
    }

    public CellStatus getCell(Position position) {
        return cells[position.getX()][position.getY()];
    }

    public void setCell(CellStatus status, Position position) {
        cells[position.getX()][position.getY()] = status;
    }

}
