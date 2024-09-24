package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook (int color, int row, int col) {
        super(color, row, col);

        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-rook");
        }
        else {
            image = getImage("/piece/black-rook");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {
            // Can move as long as it is on the same row or col as its original position
            if (targetRow == preRow || targetCol == preCol) {
                if (isValidSquare(targetRow, targetCol) && !pieceIsOnStraightLine(targetRow, targetCol)) {
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
                }
                else {
                    possibleMoves.add(new int[]{i, col});
                }
            }
        }
    }

}
