package org.example;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

// A superclass of King, Queen, Rook, Knight, Bishop, and Pawn. This class implements the
// position and color information of each piece.
public abstract class GamePiece implements Comparable {
    private int row;
    private int column;
    private boolean isWhite;
    private boolean isPromoted;
    private final int pieceNumber;
    public GamePiece(int r, int c, boolean color, boolean p, int pN) {
        row = r;
        column = c;
        isWhite = color;
        isPromoted=p;
        pieceNumber=pN;
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public void setRow(int r) {row = r;}
    public void setColumn(int c) {column = c;}
    public boolean isWhite() {
        return isWhite;
    }
    public boolean isPromoted(){return isPromoted;}
    public void move (int newRow, int newColumn) {
        row = newRow;
        column = newColumn;
    }

    public void repaint(Graphics gc) throws IOException {
        return;
    }
    public boolean validMove(GamePiece[][] board, int nextRow, int nextCol) {
        if (this.isWhite() != GameBoard.isWhitesTurn()){
            return false;
        }
        if (nextRow < 0 || nextRow >= 8 || nextCol < 0 || nextCol >= 8) {
            return false;
        }
        if (board[nextRow][nextCol]!=null && board[nextRow][nextCol].isWhite() == this.isWhite()){
            return false;
        }
        return true;
    }
    public int getPieceNumber(){
        return pieceNumber;
    }
    public void getEnPassant (boolean[] whiteEnPassant, boolean[] blackEnPassant) {

    }
    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        return pieceNumber-((GamePiece) o).getPieceNumber();
    }
    public String toString()
    {
        return "";
    }

    public boolean moved(){
        return true;
    };
    public void setMoved(boolean m){
        return;
    }
}
