package com.example.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.battleship.model.CellStatus;
import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.example.battleship.model.Position;

public class GameGridView extends View {
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private Player attackPlayer;
    private Player gridOwnerPlayer;
    private Grid grid;
    private GridDrawMode mode;

    public GameGridView(Context context) {
        this(context, null);
    }

    public GameGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(20);
    }

    public void initGrid(Grid grid) {
        this.grid = grid;
        this.mode = GridDrawMode.Creation;
    }

    public void setPlayers (Player owner, Player opposite, GridDrawMode mode) {
        this.gridOwnerPlayer = owner;
        this.attackPlayer = opposite;

        this.grid = gridOwnerPlayer.getGrid();
        this.mode = mode;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        cellWidth = getWidth() / Grid.Width;
        cellHeight = getHeight() / Grid.Height;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < Grid.Height; i++) {
            for (int j = 0; j < Grid.Width; j++) {
                switch (grid.getCells()[i][j]) {
                    case Filled:
                        if (mode == GridDrawMode.Player || mode == GridDrawMode.Creation)
                            canvas.drawRect(i * cellWidth, j * cellHeight,
                                    (i + 1) * cellWidth, (j + 1) * cellHeight,
                                    blackPaint);
                        break;
                    case Missed:
                        canvas.drawCircle(i * cellWidth + cellWidth / 2,
                                j * cellHeight + cellHeight / 2,
                                cellWidth / 4,
                                redPaint);
                        break;
                    case Attacked:
                        canvas.drawLine(
                                i * cellWidth + cellWidth / 5,
                                j * cellHeight + cellHeight / 5,
                                (i + 1) * cellWidth - cellWidth / 5,
                                (j + 1) * cellHeight - cellHeight / 5,
                                redPaint);
                        canvas.drawLine(
                                (i + 1) * cellWidth - cellWidth / 5,
                                j * cellHeight + cellHeight / 5,
                                i * cellWidth + cellWidth / 5,
                                (j + 1) * cellHeight - cellHeight / 5,
                                redPaint);
                }
            }
        }

        for (int i = 1; i < Grid.Width; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        for (int i = 1; i < Grid.Height; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int row = (int) (event.getX() / cellWidth);
                int column = (int) (event.getY() / cellHeight);

                Position p = new Position(row, column);

                if (mode == GridDrawMode.Opponent)
                    attackPlayer.AttackGrid(grid, p);

                if (mode == GridDrawMode.Creation)
                    switch (grid.getCell(p)) {
                        case Filled:
                            grid.setCell(CellStatus.Empty, p);
                            break;
                        case Empty:
                            grid.setCell(CellStatus.Filled, p);

                    }
                invalidate();
            }
        return true;
    }



}