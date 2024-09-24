package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight (int color, int row, int col) {
        super(color, row, col);

        type = Type.KNIGHT;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-knight");
        }
        else {
            image = getImage("/piece/black-knight");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol)) {

            if (Math.abs(targetRow - preRow) * Math.abs(targetCol - preCol) == 2) {
                if (isValidSquare(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void generatePossibleMoves() {
        int[][] directions = new int[][]{{-2,1},{-1,2},{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1}};
        possibleMoves = new ArrayList<int[]>();
        canCapture = new ArrayList<int[]>();
        movesCalled = true;

        for (int[] dir : directions) {
            int currX = col + dir[1];
            int currY = row + dir[0];

            if (isWithinBoard(currY, currX) && isValidSquare(currY, currX)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{currY, currX});
                }
                else {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
        }
    }

}
