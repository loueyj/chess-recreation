package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen (int color, int row, int col) {
        super(color, row, col);

        type = Type.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-queen");
        }
        else {
            image = getImage("/piece/black-queen");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {
            // Rook Movement
            if (targetRow == preRow || targetCol == preCol) {
                if (isValidSquare(targetRow, targetCol) && !pieceIsOnStraightLine(targetRow, targetCol)) {
                    return true;
                }
            }

            // Bishop Movement
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

        // Left
        for (int j = col-1; j >= 0; j--) {
            if (pieceIsOnStraightLine(row, j)) {
                break;
            }
            if (isValidSquare(row, j)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{row, j});
                }
                else {
                    possibleMoves.add(new int[]{row, j});
                }
            }
        }

        // Right
        for (int j = col+1; j <= 7; j++) {
            if (pieceIsOnStraightLine(row, j)) {
                break;
            }
            if (isValidSquare(row, j)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{row, j});
                }
                else {
                    possibleMoves.add(new int[]{row, j});
                }
            }
        }

        // Up
        for (int i = row-1; i >= 0; i--) {
            if (pieceIsOnStraightLine(i, col)) {
                break;
            }
            if (isValidSquare(i, col)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{i, col});
                }
                else {
                    possibleMoves.add(new int[]{i, col});
                }
            }
        }

        // Down
        for (int i = row+1; i <= 7; i++) {
            if (pieceIsOnStraightLine(i, col)) {
                break;
            }
            if (isValidSquare(i, col)) {
                if (hittingP != null) {
                    canCapture.add(new int[]{i, col});
                } else {
                    possibleMoves.add(new int[]{i, col});
                }
            }
        }

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
