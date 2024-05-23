package org.example;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// A representation of a pawn in crazyhouse. This class will implement the pawn's movement,
// capturing, and placing. Extends the GamePiece Class. Need to implement En Passant
public class Pawn extends GamePiece {
    private String IMG_File;
    private boolean moved;
    private boolean[] whiteCanEnPassant = new boolean[8];
    private boolean[] blackCanEnPassant = new boolean[8];
    public Pawn(int r, int c, boolean color, boolean p, boolean m, int pN) {
        super(r, c, color, p, pN);
        if (color) {
            IMG_File = "files/Chess_plt60.png";
        } else {
            IMG_File = "files/Chess_pdt60.png";
        }
        moved = moved;
    }
    @Override
    public void repaint(Graphics gc) throws IOException {
        BufferedImage img = ImageIO.read(new File(IMG_File));
        gc.drawImage(img, 100*this.getColumn(),100*this.getRow(),100, 100, null);
    }
    @Override
    public boolean validMove(GamePiece[][] board, int nextRow, int nextCol) {
        if (!super.validMove(board, nextRow, nextCol)) {
            return false;
        }
        if (!this.isWhite()){
            if (nextCol == this.getColumn()) {
                if (!moved && (nextRow-this.getRow()>2 || nextRow-this.getRow()<0 || (board[this.getRow()+1][this.getColumn()] != null || board[this.getRow()+2][this.getColumn()] != null))) {
                    return false;
                } else if (moved && (nextRow-this.getRow()>1 || nextRow-this.getRow()<0 || board[this.getRow()+1][this.getColumn()] != null)){
                    return false;
                }
            } else {
                if (this.getRow()==4 && nextRow==5 && (nextCol==this.getColumn()+1||nextCol==this.getColumn()-1) && board[4][nextCol]!=null && board[4][nextCol].getClass().getName().equals("org.example.Pawn") && whiteCanEnPassant[nextCol]) {
                    return true;
                }
                if (nextCol>this.getColumn()+1 || nextCol<this.getColumn()-1 || nextRow != this.getRow()+1 || board[nextRow][nextCol]==null || !board[nextRow][nextCol].isWhite()) {
                    return false;
                }
            }
        } else {
            if (nextCol == this.getColumn()) {
                if (!moved && (this.getRow()-nextRow>2  || this.getRow()-nextRow<0 || (board[this.getRow()-1][this.getColumn()] != null || board[this.getRow()-2][this.getColumn()] != null))) {
                    return false;
                } else if (moved && (this.getRow()-nextRow>1 || this.getRow()-nextRow<0 || board[this.getRow()-1][this.getColumn()] != null)){
                    return false;
                }
            } else {
                if (this.getRow()==3 && nextRow==2 && (nextCol==this.getColumn()+1||nextCol==this.getColumn()-1) &&board[3][nextCol]!=null&& board[3][nextCol].getClass().getName().equals("org.example.Pawn") && blackCanEnPassant[nextCol]) {
                    return true;
                }
                if (nextCol>this.getColumn()+1 || nextCol<this.getColumn()-1 || nextRow != this.getRow()-1 || board[nextRow][nextCol]==null || board[nextRow][nextCol].isWhite()) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void getEnPassant (boolean[] whiteEnPassant, boolean[] blackEnPassant) {
        whiteCanEnPassant=whiteEnPassant;
        blackCanEnPassant=blackEnPassant;
    }
    public String toString(){
        return "P";
    }
    @Override
    public boolean moved(){
        return moved;
    }
    public void setMoved(boolean m){
        moved =m;
    }
}
