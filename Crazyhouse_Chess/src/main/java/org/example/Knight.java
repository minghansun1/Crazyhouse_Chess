package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
// A representation of a knight in crazyhouse. This class will implement the knight's movement,
// capturing, and placing. Extends the GamePiece Class
public class Knight extends GamePiece {
    private String IMG_File;
    public Knight(int r, int c, boolean color, boolean p, int pN) {
        super(r, c, color, p, pN);
        if (color) {
            IMG_File = "files/Chess_nlt60.png";
        } else {
            IMG_File = "files/Chess_ndt60.png";
        }
    }
    @Override
    public void repaint(Graphics gc) throws IOException {
        BufferedImage img = ImageIO.read(new File(IMG_File));
        gc.drawImage(img, 100*this.getColumn(),100*this.getRow(), 100, 100, null);
    }
    @Override
    public boolean validMove(GamePiece[][] board, int nextRow, int nextCol) {
        if (!super.validMove(board, nextRow, nextCol)) {
            return false;
        }
        if ((Math.abs(nextRow - this.getRow())!=2 || Math.abs(nextCol - this.getColumn())!=1) && (Math.abs(nextRow - this.getRow())!=1 || Math.abs(nextCol - this.getColumn())!=2)) {
            return false;
        }
        return true;
    }
    public String toString(){
        return "N";
    }
}
