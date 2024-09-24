package Pieces;

import GUI.Board;
import GUI.GamePanel;
import GUI.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int row, col, preRow, preCol;
    public int color;
    public Piece hittingP;
    public List<int[]> possibleMoves = new ArrayList<int[]>();
    public List<int[]> canCapture = new ArrayList<int[]>();
    public boolean movesCalled = false;
    public boolean moved, twoStepped;

    public Piece(int color, int row, int col) {

        this.color = color;
        this.row = row;
        this.col = col;
        x = getX(col);
        y = getY(row);
        preRow = row;
        preCol = col;
    }

    // Import pieces as buffered images
    public BufferedImage getImage(String imagePath) {

        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            if (GamePanel.simPieces.get(i) == this) {
                return i;
            }
        }
        return 0;
    }

    public void updatePosition() {

        // En passant checker
        if (type == Type.PAWN) {
            if (Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preRow = getRow(y);
        preCol = getCol(x);
        moved = true;
    }

    public void resetPosition() {
        row = preRow;
        col = preCol;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetRow, int targetCol) {
        return false;
    }

    public boolean isWithinBoard(int targetRow, int targetCol) {
        return targetRow >= 0 && targetRow <= 7 && targetCol >= 0 && targetCol <= 7;
    }

    public boolean isSameSquare(int targetRow, int targetCol) {
        return targetRow == preRow && targetCol == preCol;
    }

    public Piece getHittingP(int targetRow, int targetCol) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.row == targetRow && piece.col == targetCol && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetRow, int targetCol) {

        hittingP = getHittingP(targetRow, targetCol);

        // Determine whether the square is occupied by another piece
        if (hittingP == null) {
            return true;
        }
        else {
            if (hittingP.color != this.color) {
                return true;
            }
            else {
                hittingP = null;
            }
        }

        return false;
    }

    public boolean pieceIsOnStraightLine(int targetRow, int targetCol) {

        // When piece is moving to the left
        for (int j = preCol-1; j > targetCol; j--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == j && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // When piece is moving to the right
        for (int j = preCol+1; j < targetCol; j++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == j && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // When piece is moving up
        for (int i = preRow-1; i > targetRow; i--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.row == i && piece.col == targetCol) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // When piece is moving down
        for (int i = preRow+1; i < targetRow; i++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.row == i && piece.col == targetCol) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetRow, int targetCol) {

        if (targetRow < preRow) {
            // Up Left
            for (int j = preCol-1; j > targetCol; j--) {
                int diff = Math.abs(j - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == j && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

            // Up Right
            for (int j = preCol+1; j < targetCol; j++) {
                int diff = Math.abs(j - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == j && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if (targetRow > preRow) {
            // Down Left
            for (int j = preCol-1; j > targetCol; j--) {
                int diff = Math.abs(j - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == j && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

            // Down Right
            for (int j = preCol+1; j < targetCol; j++) {
                int diff = Math.abs(j - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == j && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void generatePossibleMoves() {
        return;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

}
