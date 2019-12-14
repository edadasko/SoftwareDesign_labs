package com.example.battleship;

import com.example.battleship.model.CellStatus;
import com.example.battleship.model.Grid;
import com.example.battleship.model.Position;

public class GridValidator {
    private int currentNumOf4 = 0;
    private int currentNumOf3 = 0;
    private int currentNumOf2 = 0;
    private int currentNumOf1 = 0;

    private Grid grid;
    private boolean[][] checkedCells;

    public GridValidator(Grid grid) {
        this.grid = grid;
        this.checkedCells = new boolean[Grid.Height][Grid.Width];
    }

    public boolean Validate() {
        final int requiredNumOf4 = 1;
        final int requiredNumOf3 = 2;
        final int requiredNumOf2 = 3;
        final int requiredNumOf1 = 4;

        for (int i = 0; i < Grid.Height; i++)
            for (int j = 0; j < Grid.Width; j++)
                if (!checkCell(i, j))
                    return false;

        return currentNumOf4 == requiredNumOf4 &&
                currentNumOf3 == requiredNumOf3 &&
                currentNumOf2 == requiredNumOf2 &&
                currentNumOf1 == requiredNumOf1;
    }

    private boolean checkCell(int x, int y) {
        if (checkedCells[x][y])
            return true;

        CellStatus thisCell = grid.getCell(new Position(x, y));

        if (thisCell == CellStatus.Empty)
            return true;

        int lengthOfShip = 1;

        CellStatus left = x > 0 ? grid.getCell(new Position(x - 1, y)) : null;
        CellStatus right = x != Grid.Width - 1 ? grid.getCell(new Position(x + 1, y)) : null;
        CellStatus up = y > 0 ? grid.getCell(new Position(x, y - 1)) : null;
        CellStatus down = y != Grid.Height - 1 ? grid.getCell(new Position(x, y + 1)) : null;

        int countOfFilledNeighbour = 0;
        if (left == CellStatus.Filled)
            countOfFilledNeighbour++;
        if (right == CellStatus.Filled)
            countOfFilledNeighbour++;
        if (up == CellStatus.Filled)
            countOfFilledNeighbour++;
        if (down == CellStatus.Filled)
            countOfFilledNeighbour++;

        if (countOfFilledNeighbour > 1)
            return false;

        if (left == CellStatus.Filled) {
            int i = x - 1;
            while (i >= 0 && grid.getCell(new Position(i, y)) == CellStatus.Filled) {
                lengthOfShip++;
                checkedCells[i][y] = true;
                i--;
            }
        } else if (right == CellStatus.Filled) {
            int i = x + 1;
            while (i < Grid.Width && grid.getCell(new Position(i, y)) == CellStatus.Filled) {
                lengthOfShip++;
                checkedCells[i][y] = true;
                i++;
            }
        } else if (up == CellStatus.Filled) {
            int i = y - 1;
            while (i >= 0 && grid.getCell(new Position(x, i)) == CellStatus.Filled) {
                lengthOfShip++;
                checkedCells[x][i] = true;
                i--;
            }
        } else {
            int i = y + 1;
            while (i < Grid.Height && grid.getCell(new Position(x, i)) == CellStatus.Filled) {
                lengthOfShip++;
                checkedCells[x][i] = true;
                i++;
            }
        }

        switch (lengthOfShip) {
            case 1:
                currentNumOf1++;
                return true;
            case 2:
                currentNumOf2++;
                return true;
            case 3:
                currentNumOf3++;
                return true;
            case 4:
                currentNumOf4++;
                return true;
            default:
                return false;
        }
    }
}
