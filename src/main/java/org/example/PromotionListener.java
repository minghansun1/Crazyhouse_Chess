package org.example;

import java.awt.*;

public interface PromotionListener {
    void pawnPromoted(int row, boolean isWhite);
    int getPromotedPiece();
    public void paintComponent(Graphics gc);
    public void toggleVisibility();
}
