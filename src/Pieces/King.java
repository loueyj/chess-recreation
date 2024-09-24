package Pieces;

import GUI.GamePanel;
import GUI.Type;

import java.util.ArrayList;

public class King extends Piece {

    public King (int color, int row, int col) {
        super(color, row, col);

        type = Type.KING;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-king");
        }
        else {
            image = getImage("/piece/black-king");
        }
    }

    public boolean canMove(int targetRow, int targetCol) {

        if (isWithinBoard(targetRow, targetCol)) {
            // Movement
            if (Math.abs(targetRow - preRow) + Math.abs(targetCol - preCol) == 1
                    || Math.abs(targetRow - preRow) * Math.abs(targetCol - preCol) == 1) {

                if (isValidSquare(targetRow, targetCol)) {
                    return true;
                }
            }

            // Castling
            if (!moved) {
                // Right Castling
                if (targetRow == preRow && targetCol == preCol+2 && !pieceIsOnStraightLine(targetRow, targetCol)) {
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.row == preRow && piece.col == preCol+3 && !piece.moved) {
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }
                // Left Castling
                if (targetRow == preRow && targetCol == preCol-2 && !pieceIsOnStraightLine(targetRow, targetCol)) {
                    Piece p[] = new Piece[2];
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.row == targetRow && piece.col == preCol-3) {
                            p[0] = piece;
                        }
                        if (piece.row == targetRow && piece.col == preCol-4) {
                            p[1] = piece;
                        }
                        if (p[0] == null && p[1] != null && !p[1].moved) {
                            GamePanel.castlingP = p[1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void generatePossibleMoves() { // Add in castling moves
        int[][] directions;
        possibleMoves = new ArrayList<int[]>();
        canCapture = new ArrayList<int[]>();
        movesCalled = true;
        boolean rightCastle = false;
        boolean leftCastle = false;

        if (!moved) {
            if (canMove(row, col+2)) {
                rightCastle = true;
            }
            if (canMove(row, col-3)) {
                leftCastle = true;
            }
        }

        if (rightCastle && leftCastle) {
            directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{0,2},{0,-2}};
        }
        else if (rightCastle) {
            directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{0,2}};
        }
        else if (leftCastle) {
            directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{0,-2}};
        }
        else {
            directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
        }

        for (int[] dir : directions) {
            int currX = col + dir[1];
            int currY = row + dir[0];
            boolean legal = true;

            if (isWithinBoard(currY, currX) && isValidSquare(currY, currX)) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.color != color && piece.canMove(currY, currX)) {
                        legal = false;
                        break;
                    }
                    if (piece != this && piece.color != this.color && piece.canMove(this.row, this.col)) {
                        legal = false;
                        break;
                    }
                    if (piece.color != this.color && piece.canMove(this.row, this.col)) {
                        legal = false;
                        break;
                    }
                }
                if (legal) {
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
}
