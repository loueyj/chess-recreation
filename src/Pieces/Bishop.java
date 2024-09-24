package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop (int color, int row, int col) {
        super(color, row, col);

        type = Type.BISHOP;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-bishop");
        }
        else {
            image = getImage("/piece/black-bishop");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {

            if (Math.abs(targetRow - preRow) == Math.abs(targetCol - preCol)) {
                if (isValidSquare(targetRow, targetCol) && !pieceIsOnDiagonalLine(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void generatePossibleMoves() {
        possibleMoves = new ArrayList<int[]>();
        canCapture = new ArrayList<int[]>();
        movesCalled = true;

        // Up Left
        int currX = col-1;
        int currY = row-1;
        while (currX >= 0 && currY >= 0) {
            if (pieceIsOnDiagonalLine(currY, currX)) {
                break;
            }
            if (isValidSquare(currY, currX)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{currY, currX});
                }
                else {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
            currY--;
            currX--;
        }

        // Up Right
        currX = col+1;
        currY = row-1;
        while (currX <= 7 && currY >= 0) {
            if (pieceIsOnDiagonalLine(currY, currX)) {
                break;
            }
            if (isValidSquare(currY, currX)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{currY, currX});
                }
                else {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
            currY--;
            currX++;
        }

        // Down Left
        currX = col-1;
        currY = row+1;
        while (currX >= 0 && currY <= 7) {
            if (pieceIsOnDiagonalLine(currY, currX)) {
                break;
            }
            if (isValidSquare(currY, currX)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{currY, currX});
                }
                else {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
            currY++;
            currX--;
        }

        // Down Right
        currX = col+1;
        currY = row+1;
        while (currX <= 7 && currY <= 7) {
            if (pieceIsOnDiagonalLine(currY, currX)) {
                break;
            }
            if (isValidSquare(currY, currX)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{currY, currX});
                }
                else {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
            currY++;
            currX++;
        }
    }
}
