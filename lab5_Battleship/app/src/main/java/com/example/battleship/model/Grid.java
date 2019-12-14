package com.example.battleship.model;

import java.security.InvalidParameterException;

public class Grid {
    public static final int Height = Position.MaxX + 1;
    public static final int Width = Position.MaxY + 1;

    private CellStatus[][] cells;

    public Grid() {
        cells = new CellStatus[Height][Width];

        for (int i = 0; i < Height; i++)
            for (int j = 0; j < Width; j++)
                cells[i][j] = CellStatus.Empty;
    }

    public void SetShip(Ship ship, Position position) {
        int x = position.getX();
        int y = position.getY();

        ShipOrientation orientation = ship.getOrientation();

        for (int i = 0; i < ship.getSize(); i++) {
            if (cells[x][y] == CellStatus.Filled || x >= Height || y >= Width ) {
                throw new InvalidParameterException("Invalid position");
            }
            cells[x][y] = CellStatus.Filled;
            switch (orientation) {
                case Vertical:
                    y++;
                    break;
                case Horizontal:
                    x++;
                    break;
            }
        }
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
