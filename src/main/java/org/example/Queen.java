package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
// A representation of a queen in crazyhouse. This class will implement the queen's movement,
// capturing, and placing. Extends the GamePiece Class
public class Queen extends GamePiece {
    private String IMG_File;
    public Queen(int r, int c, boolean color, boolean p, int pN) {
        super(r, c, color, p, pN);
        if (color) {
            IMG_File = "files/Chess_qlt60.png";
        } else {
            IMG_File = "files/Chess_qdt60.png";
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
        if (Math.abs(nextRow - this.getRow()) != Math.abs(nextCol - this.getColumn()) && nextRow != this.getRow() && nextCol != this.getColumn()) {
            return false;
        }
        if (Math.abs(nextRow - this.getRow()) == Math.abs(nextCol - this.getColumn())) {
            if (nextRow > this.getRow() && nextCol > this.getColumn()) {
                for(int i = 1; i < nextRow - this.getRow(); i++) {
                    if(board[this.getRow()+i][this.getColumn()+i] != null){
                        return false;
                    }
                }
            } else if (nextRow > this.getRow() && nextCol < this.getColumn()) {
                for(int i = 1; i < nextRow - this.getRow(); i++) {
                    if(board[this.getRow()+i][this.getColumn()-i] != null){
                        return false;
                    }
                }
            } else if (nextRow < this.getRow() && nextCol > this.getColumn()) {
                for(int i = 1; i < this.getRow() - nextRow; i++) {
                    if(board[this.getRow()-i][this.getColumn()+i] != null){
                        return false;
                    }
                }
            } else if (nextRow < this.getRow() && nextCol < this.getColumn()) {
                for(int i = 1; i < this.getRow() - nextRow; i++) {
                    if(board[this.getRow()-i][this.getColumn()-i] != null){
                        return false;
                    }
                }
            }
        }
        if (nextRow == this.getRow() || nextCol == this.getColumn()) {
            if (nextRow < this.getRow()) {
                for(int i = nextRow + 1; i<this.getRow(); i++){
                    if (board[i][this.getColumn()] != null) {
                        return false;
                    }
                }
            } else if (nextRow > this.getRow()) {
                for(int i = this.getRow() + 1; i < nextRow; i++){
                    if (board[i][this.getColumn()] != null) {
                        return false;
                    }
                }
            } else if (nextCol < this.getColumn()) {
                for(int i = nextCol + 1; i<this.getColumn(); i++){
                    if (board[this.getRow()][i] != null) {
                        return false;
                    }
                }
            } else if (nextCol > this.getColumn()) {
                for(int i = this.getColumn() + 1; i<nextCol; i++){
                    if (board[this.getRow()][i] != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    public String toString(){
        return "Q";
    }
}
