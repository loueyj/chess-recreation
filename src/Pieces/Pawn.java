package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn (int color, int row, int col) {
        super(color, row, col);

        type = Type.PAWN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-pawn");
        }
        else {
            image = getImage("/piece/black-pawn");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {

            // Determine direction based on piece color
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            }
            else {
                moveValue = 1;
            }

            // Find the hitting piece if there is one
            hittingP = getHittingP(targetRow, targetCol);

            // Normal movement
            if (targetRow == preRow + moveValue && targetCol == preCol && hittingP == null) {
                return true;
            }

            // First move movement
            if (targetRow == preRow + moveValue*2 && targetCol == preCol && hittingP == null && !moved && !pieceIsOnStraightLine(targetRow, targetCol)) {
                return true;
            }

            // Capture movement
            if (targetRow == preRow + moveValue && Math.abs(targetCol - preCol) == 1 && hittingP != null && hittingP.color != color) {
                return true;
            }

            // En passant
            if (targetRow == preRow + moveValue && Math.abs(targetCol - preCol) == 1) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == preRow && piece.col == targetCol && piece.twoStepped) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void generatePossibleMoves() {
        int[][] directions;
        possibleMoves = new ArrayList<int[]>();
        canCapture = new ArrayList<int[]>();
        movesCalled = true;

        int moveValue;
        if (color == GamePanel.WHITE) {
            moveValue = -1;
        }
        else {
            moveValue = 1;
        }

        if (!moved) {
            directions = new int[][]{{moveValue,-1},{moveValue,1},{moveValue,0},{moveValue*2,0}};
        }
        else {
            directions = new int[][]{{moveValue,-1},{moveValue,1},{moveValue,0}};
        }

        for (int[] dir : directions) {
            int currX = col + dir[1];
            int currY = row + dir[0];

            if (isWithinBoard(currY, currX) && isValidSquare(currY, currX)) {
                if (dir[1] != 0 && hittingP != null && hittingP.color != color) {
                    canCapture.add(new int[]{currY, currX});
                }
                else if (dir[1] == 0 && canMove(currY, currX)) {
                    possibleMoves.add(new int[]{currY, currX});
                }
            }
        }

        int[][] enPassantDirections = new int[][]{{0,-1},{0,1}};

        for (int[] dir : enPassantDirections) {
            int currX = col + dir[1];
            int currY = row + dir[0];

            if (isWithinBoard(currY, currX) && isValidSquare(currY, currX)) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == currY && piece.col == currX && piece.twoStepped) {
                        possibleMoves.add(new int[]{currY+moveValue, currX});
                    }
                }
            }
        }

    }

}
