package org.example;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// A representation of a king in crazyhouse. This class will implement the king's movement,
// capturing, and placing. Extends the GamePiece Class
public class King extends GamePiece {
    private String IMG_File;
    private boolean canCastle =true;
    public King(int r, int c, boolean color, boolean p, int pN) {
        super(r, c, color, p, pN);
        if (color) {
            IMG_File = "files/Chess_klt60.png";
        } else {
            IMG_File = "files/Chess_kdt60.png";
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
        if (Math.abs(nextRow - this.getRow())>1 || Math.abs(nextCol - this.getColumn())>1) {
            return false;
        }
        return true;
    }
    @Override
    public String toString(){
        return "K";
    }
}
