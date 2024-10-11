package GUI;

import Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    // Number of time per second that the game will update calling update() and paintComponent()
    private static final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>(); // Backup list
    public static ArrayList<Piece> simPieces = new ArrayList<>(); // Used to keep track of pieces on the board
    public static ArrayList<Piece> promotionPieces = new ArrayList<>(); // Used to show available choices when promotion is made
    public static ArrayList<Piece> pieceMoves = new ArrayList<>(); // Used for displaying notation
    public static ArrayList<String> notationList = new ArrayList<>();
    public static Piece castlingP;
    Piece activeP, checkingP;

    // Colors
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    // Player to move
    int currentColor = WHITE;
    int count = 0;
    int lastRow, lastCol;

    // Sound
    public static Sound se = new Sound();

    // Checkers
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean stalemate;
    boolean pieceTaken;
    boolean leftCastle;
    boolean rightCastle;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        // Calls the start method (run()). Starting a thread calls the run method.
        gameThread.start();
    }

    // Add the pieces to the board
    public void setPieces() {

        // White Pieces
        pieces.add(new Pawn(WHITE, 6, 0));
        pieces.add(new Pawn(WHITE, 6, 1));
        pieces.add(new Pawn(WHITE, 6, 2));
        pieces.add(new Pawn(WHITE, 6, 3));
        pieces.add(new Pawn(WHITE, 6, 4));
        pieces.add(new Pawn(WHITE, 6, 5));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 6, 7));
        pieces.add(new Rook(WHITE, 7, 0));
        pieces.add(new Knight(WHITE, 7, 1));
        pieces.add(new Bishop(WHITE, 7, 2));
        pieces.add(new Queen(WHITE, 7, 3));
        pieces.add(new King(WHITE, 7, 4));
        pieces.add(new Bishop(WHITE, 7, 5));
        pieces.add(new Knight(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 7, 7));

        // Black Pieces
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Knight(BLACK, 0, 1));
        pieces.add(new Bishop(BLACK, 0, 2));
        pieces.add(new Queen(BLACK, 0, 3));
        pieces.add(new King(BLACK, 0, 4));
        pieces.add(new Bishop(BLACK, 0, 5));
        pieces.add(new Knight(BLACK, 0, 6));
        pieces.add(new Rook(BLACK, 0, 7));
        pieces.add(new Pawn(BLACK, 1, 0));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 1, 2));
        pieces.add(new Pawn(BLACK, 1, 3));
        pieces.add(new Pawn(BLACK, 1, 4));
        pieces.add(new Pawn(BLACK, 1, 5));
        pieces.add(new Pawn(BLACK, 1, 6));
        pieces.add(new Pawn(BLACK, 1, 7));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    // Required to implement Runnable
    // Creates game loop
    public void run() {
        // GAME LOOP
        double drawInterval = (double) 1000000000 /FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // Update information (piece positions, number of pieces that are left on the board, etc.)
    private void update() {

        if (promotion) {
            promoting();
        }
        else if (!gameOver && !stalemate) {
            // MOUSE BUTTON PRESSED
            if (mouse.pressed) {
                if (activeP == null) {
                    // If there is no active piece (no piece currently picked up by mouse), then check if there is a piece at mouse location to pick up
                    for (Piece piece : simPieces) {
                        // If mouse is on a piece corresponding to the color turn, pick up the piece and set it to activeP
                        if (piece.color == currentColor && piece.row == mouse.y/Board.SQUARE_SIZE && piece.col == mouse.x/Board.SQUARE_SIZE) {
                            activeP = piece;
                        }
                    }
                }
                else {
                    // If the mouse is holding a piece, simulate the move
                    if (!activeP.movesCalled) {
                        activeP.generatePossibleMoves(); // Probably need to change to only happen once until a move is made
                    }
                    simulate();
                }
            }

            // MOUSE BUTTON RELEASED
            if (!mouse.pressed) {
                if (activeP != null) {

                    // Confirmed that move is possible and being made
                    if (validSquare) {

                        lastRow = activeP.preRow;
                        lastCol = activeP.preCol;

                        // Update the piece list in the event that a piece has been captured and removed in simulate()
                        pieceMoves.add(activeP);
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        activeP.movesCalled = false;
                        count++;

                        if (activeP.hittingP != null) {
                            pieceTaken = true;
                            if (!isKingInCheck()) {
                                se.play(2, false);
                            }
                            else {
                                se.play(1, false);
                            }
                        }
                        else {
                            pieceTaken = false;
                            if (!isKingInCheck() && castlingP == null) {
                                se.play(0, false);
                            }
                            else if (isKingInCheck()) {
                                se.play(1, false);
                            }
                        }

                        if (castlingP != null) {
                            castlingP.updatePosition();
                            if (!isKingInCheck()) {
                                se.play(3, false);
                            }
                        }

                        if (isKingInCheck() && isCheckMate()) {
                            gameOver = true;
                            se.play(1, false);
                            if (count > 0) {
                                addNotation();
                            }
                        }
                        else if (isStalemate() && !isKingInCheck()) {
                            stalemate = true;
                            if (count > 0) {
                                addNotation();
                            }
                        }
                        else {
                            if (canPromote()) {
                                promotion = true;
                                se.play(5, false);
                            }
                            else {
                                if (count > 0) {
                                    addNotation();
                                }
                                changePlayer();
                                leftCastle = false;
                                rightCastle = false;
                            }
                        }
                    }
                    else { // Move not valid so everything will be put back to previous positions
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                        se.play(4, false);
                    }
                }
            }
        }


    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        // Reset piece list during every loop
        // Done to restore removed pieces during the simulation
        copyPieces(pieces, simPieces);

        // Reset the castling piece's position
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        // If a piece is being held, update its position based on the mouse position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.row = activeP.getRow(activeP.y);
        activeP.col = activeP.getCol(activeP.x);

        // Check if the piece is hovering over a valid square
        if (activeP.canMove(activeP.row, activeP.col)) {
            canMove = true;

            // If the piece is hitting a valid piece to capture, remove it from the list
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            if (!isIllegal(activeP) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }

    public boolean isIllegal(Piece king) {

        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.row, king.col)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean opponentCanCaptureKing() {

        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.row, king.col)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingInCheck() {

        Piece king = getKing(true);

        if (activeP.canMove(king.row, king.col)) {
            checkingP = activeP;
            return true;
        }
        else {
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {

        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            }
            else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }

        return king;
    }

    private boolean isCheckMate() {

        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        }
        else {
            // Check to see if the attack can be blocked with another piece

            // Position of the checking piece relative to the king
            int rowDiff = Math.abs(checkingP.row - king.row);
            int colDiff = Math.abs(checkingP.col - king.col);

            if (rowDiff == 0) { // The checking piece is on the same row
                if (checkingP.col < king.col) {
                    // Checking columns to the left of the king
                    for (int j = checkingP.col; j < king.col; j++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.row, j)) {
                                return false;
                            }
                        }
                    }
                }
                else {
                    // Checking columns to the right of the king
                    for (int j = checkingP.col; j > king.col; j--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.row, j)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (colDiff == 0) { // The checking piece is on the same column
                if (checkingP.row < king.row) {
                    // Checking rows above the king
                    for (int i = checkingP.row; i < king.row; i++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(i, checkingP.col)) {
                                return false;
                            }
                        }
                    }
                }
                else {
                    // Checking rows below the king
                    for (int i = checkingP.row; i > king.row; i--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(i, checkingP.col)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (rowDiff == colDiff) { // The checking piece is on the same diagonal
                // Checking rows above the king
                if (checkingP.row < king.row) {
                    // Checking columns to the left of the king and above
                    if (checkingP.col < king.col) {
                        for (int i = checkingP.row, j = checkingP.col; j < king.col; i++, j++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(i, j)) {
                                    return false;
                                }
                            }
                        }
                    }
                    else { // Checking columns to the right of the king and above
                        for (int i = checkingP.row, j = checkingP.col; j > king.col; i++, j--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(i, j)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                else { // Checking rows below the king
                    // Checking columns to the left of the king and below
                    if (checkingP.col < king.col) {
                        for (int i = checkingP.row, j = checkingP.col; j < king.col; i--, j++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(i, j)) {
                                    return false;
                                }
                            }
                        }
                    }
                    else { // Checking columns to the right of the king and below
                        for (int i = checkingP.row, j = checkingP.col; j > king.col; i--, j--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(i, j)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    // Check to see if there are any squares where the king can move to
    private boolean kingCanMove(Piece king) {

        int[][] directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};

        for (int[] dir : directions) {
            if (isValidMove(king, dir[0], dir[1])) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidMove(Piece king, int currRow, int currCol) {

        boolean validMove = false;

        king.row += currRow;
        king.col += currCol;

        if (king.canMove(king.row, king.col)) {

            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }

            if (!isIllegal(king)) {
                validMove = true;
            }
        }

        // Reset the position of the king and the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return validMove;
    }

    private boolean isStalemate() {

        int playerCount = 0;
        int opponentCount = 0;
        // Count the number of opponent pieces
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                playerCount++;
            }
            else {
                opponentCount++;
            }
        }

        if (playerCount == 1 && !kingCanMove(getKing(true))) {
            return true;
        }
        else if (playerCount == 1 && opponentCount == 1) {
            return true;
        }
        else {
            for (Piece piece : simPieces) {
                piece.generatePossibleMoves();
                if (piece.color == currentColor && (!piece.canCapture.isEmpty() || !piece.possibleMoves.isEmpty())) {
                    return false;
                }
            }
        }


        return true;
    }

    private void addNotation() {
        Piece lastPiece = pieceMoves.get(pieceMoves.size()-1);
        String notationMove = "";

        if (castlingP != null && leftCastle) {
            notationList.add("O-O-O");
            return;
        }
        if (castlingP != null && rightCastle) {
            notationList.add("O-O");
            return;
        }

        switch (lastPiece.type) {
            case KING:
                notationMove = "K";
                break;
            case QUEEN:
                notationMove = "Q";
                break;
            case KNIGHT:
                notationMove = "N";
                break;
            case ROOK:
                notationMove = "R";
                break;
            case BISHOP:
                notationMove = "B";
                break;
            case PAWN:
                if (pieceTaken) {
                    notationMove = String.valueOf((char) (lastCol + 'a'));
                }
                break;
        }

        String notationRow = String.valueOf(8 - lastPiece.row);
        String notationCol = String.valueOf((char) (lastPiece.col + 'a'));

        if (pieceTaken) {
            notationMove += "x";
        }

        notationMove += notationCol + notationRow;

        if (promotion && !simPieces.isEmpty()) {
            switch (simPieces.get(simPieces.size()-1).type) {
                case QUEEN:
                    notationMove += "=Q";
                    break;
                case KNIGHT:
                    notationMove += "=N";
                    break;
                case ROOK:
                    notationMove += "=R";
                    break;
                case BISHOP:
                    notationMove += "=B";
                    break;
                default:
                    break;
            }
        }

        if (checkingP != null || simPieces.get(simPieces.size()-1).canMove(getKing(true).row, getKing(true).col)) {
            if (isCheckMate()) {
                notationMove += "#";
            }
            else {
                notationMove += "+";
            }
        }

        notationList.add(notationMove);
    }

    private void drawNotation(Graphics2D g2) {
        int notationCounter = 0;

        for (String s : notationList) {
            if (notationCounter % 2 == 0) {
                g2.drawString(s, 840, notationCounter / 2 * 25 + 25);
            }
            else {
                g2.drawString(s, 1040, notationCounter / 2 * 25 + 25);
            }
            notationCounter++;
        }
    }

    private void checkCastling() {

        if (castlingP != null) {
            // Left Rook
            if (castlingP.col == 0) {
                castlingP.col += 3;
                leftCastle = true;
            }
            // Right Rook
            else if (castlingP.col == 7) {
                castlingP.col -= 2;
                rightCastle = true;
            }
            // Update rook position after castling
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer() {

        if (currentColor == WHITE) {
            currentColor = BLACK;
            // Reset twoStepped status to false for the black piece after move is completed
            for (Piece piece : simPieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor = WHITE;
            // Reset twoStepped status to false for the white piece after move is completed
            for (Piece piece : simPieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }

        activeP = null;
    }

    // Checks to see if promotion is possible and creates promotionPieces if it is
    private boolean canPromote() {

        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0) {
                promotionPieces.clear();
                promotionPieces.add(new Queen(currentColor, activeP.row, activeP.col));
                promotionPieces.add(new Knight(currentColor, activeP.row+1, activeP.col));
                promotionPieces.add(new Rook(currentColor, activeP.row+2, activeP.col));
                promotionPieces.add(new Bishop(currentColor, activeP.row+3, activeP.col));
                // Add another piece to exit out and cancel the promotion
                return true;
            }
            else if (currentColor == BLACK && activeP.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Queen(currentColor, activeP.row, activeP.col));
                promotionPieces.add(new Knight(currentColor, activeP.row-1, activeP.col));
                promotionPieces.add(new Rook(currentColor, activeP.row-2, activeP.col));
                promotionPieces.add(new Bishop(currentColor, activeP.row-3, activeP.col));
                // Add another piece to exit out and cancel the promotion
                return true;
            }
        }

        return false;
    }

    private void promoting() {

        if (mouse.pressed) {
            for (Piece piece : promotionPieces) {
                if (piece.row == mouse.y / Board.SQUARE_SIZE && piece.col == mouse.x / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.row, activeP.col));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.row, activeP.col));
                            break;
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.row, activeP.col));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.row, activeP.col));
                            break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    addNotation();
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    // Handles all drawing (drawing of board, pieces, etc.)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Needs to be Graphics2D to match the board
        Graphics2D g2 = (Graphics2D) g;

        // Draw Board
        board.draw(g2);

        // Draw coordinates onto the board


        // Draw yellow squares to show what the last move made was
        if (count > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f));
            g2.setColor(Color.yellow);
            g2.fillRect(lastCol * Board.SQUARE_SIZE, lastRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            g2.fillRect(pieceMoves.get(pieceMoves.size()-1).col * Board.SQUARE_SIZE, pieceMoves.get(pieceMoves.size()-1).row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
        }

        // Draw Pieces
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
        g2.setColor(Color.white);
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        // Draw all notations of moves
        drawNotation(g2);

        if (activeP != null) {

            // Draw transparent dots on all of the squares which the active piece can move to
            if (!activeP.possibleMoves.isEmpty() && !gameOver && !stalemate) { // Need to add big circle on top of piece that can be captured instead of small circle under
                g2.setColor(Color.black);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                for (int[] moves : activeP.possibleMoves) { // Add method to generate list of canCapture
                    g2.fillOval(moves[1] * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE - Board.SQUARE_SIZE/6, moves[0] * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE - Board.SQUARE_SIZE/6, Board.SQUARE_SIZE/3, Board.SQUARE_SIZE/3);
                }
            }
            // Draw circle around pieces which the active piece can capture
            if (!activeP.canCapture.isEmpty() && !gameOver && !stalemate) {
                g2.setColor(Color.black);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                for (int [] moves : activeP.canCapture) {
                    Area a = new Area(new Ellipse2D.Double(moves[1] * Board.SQUARE_SIZE, moves[0] * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE));
                    a.subtract(new Area(new Ellipse2D.Double(moves[1] * Board.SQUARE_SIZE + (double) Board.SQUARE_SIZE / 12, moves[0] * Board.SQUARE_SIZE + (double) Board.SQUARE_SIZE / 12, Board.SQUARE_SIZE - (double) Board.SQUARE_SIZE / 6, Board.SQUARE_SIZE - (double) Board.SQUARE_SIZE / 6)));
                    g2.fill(a);
                }
            }

            if (activeP.isWithinBoard(activeP.row, activeP.col)) {
                // Set the piece color to match the active piece color
                g2.setColor(Color.white);
                // make the piece semi-transparent
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                // color the boarders of current tile being hovered over to show where the piece will be placed when the mouse click is released
                Area a = new Area(new Rectangle(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE));
                a.subtract(new Area(new Rectangle(activeP.col * Board.SQUARE_SIZE + 5, activeP.row * Board.SQUARE_SIZE + 5, 90, 90)));
                g2.fill(a);
                // set the piece transparency back to default
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // WORKS BUT CHANGE SO THAT RED BACKGROUND IS ONLY DISPLAYED AFTER MOUSE RELEASE AND IS UNDER THE KING
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    Piece kingInCheck = getKing(false);
                    g2.fillRect(kingInCheck.col * Board.SQUARE_SIZE, kingInCheck.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                }
            }

            // Draw the active piece in the end so that it will not be covered by the board or the tile
            activeP.draw(g2);
        }

        // STATUS MESSAGES
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.setColor(Color.white);
            if (activeP.row == 0) {
                g2.fillRect(promotionPieces.get(0).col * Board.SQUARE_SIZE, promotionPieces.get(0).row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE * 4);
            }
            else {
                g2.fillRect(promotionPieces.get(0).col * Board.SQUARE_SIZE, promotionPieces.get(promotionPieces.size()-1).row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE * 4);
            }
            for (Piece piece : promotionPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }

        if (gameOver) {

            String s = "";

            if (currentColor == WHITE) {
                s = "White Wins";
            }
            else {
                s = "Black Wins";
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 420);
        }

        if (stalemate) {
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.lightGray);
            g2.drawString("Stalemate", 200, 420);
        }
    }

}
