package GUI;

import java.awt.*;

public class Board {

    private static final int MAX_ROW = 8;
    private static final int MAX_COL = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    public void draw(Graphics2D g2) {

        boolean tileColor = false;

        for (int row = 0; row < MAX_ROW; row++) {

            for (int col = 0; col < MAX_COL; col++) {

                if (tileColor) {
                    if (row % 2 == 0) {
                        g2.setColor(new Color(118, 150, 86));
                    }
                    else {
                        g2.setColor(new Color(238, 238, 210));
                    }
                    tileColor = false;
                }
                else {
                    if (row % 2 == 0) {
                        g2.setColor(new Color(238, 238, 210));
                    }
                    else {
                        g2.setColor(new Color(118, 150, 86));
                    }
                    tileColor = true;
                }

                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
