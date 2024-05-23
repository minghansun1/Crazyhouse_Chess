package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

// The board where the game is played. Is a JComponent which displays game. Also handles user clicks and drags.
public class GameBoard extends JButton {
    private static int squareWidth = 100;
    private GamePiece[][] board = new GamePiece[8][8];
    private TreeSet<GamePiece> whitePiecesOnBoard = new TreeSet<>();
    private TreeSet<GamePiece> blackPiecesOnBoard = new TreeSet<>();
    private int[] capturedPieces = new int[10];
    private ArrayList<String> moveHistory;
    private static boolean whitesTurn = true;
    private int currRow = -1;
    private int currCol = -1;
    private int nextRow = -1;
    private int nextCol = -1;
    private int promotingRow=-1;
    private int whiteKingRow;
    private int whiteKingCol;
    private int blackKingRow;
    private int blackKingCol;
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private int pieceNumber = 0;
    private ArrayList<PromotionListener> promotionListeners = new ArrayList<>();
    private ArrayList<CheckmateListener> checkmateListeners = new ArrayList<>();
    private boolean promoting = false;
    private boolean[] whiteCanEnPassant = new boolean[8];
    private boolean[] blackCanEnPassant = new boolean[8];
    private BufferedImage wq = ImageIO.read(new File("files/Chess_qlt60.png"));
    private BufferedImage wr = ImageIO.read(new File("files/Chess_rlt60.png"));
    private BufferedImage wb = ImageIO.read(new File("files/Chess_blt60.png"));
    private BufferedImage wn = ImageIO.read(new File("files/Chess_nlt60.png"));
    private BufferedImage wp = ImageIO.read(new File("files/Chess_plt60.png"));
    private BufferedImage bq = ImageIO.read(new File("files/Chess_qdt60.png"));
    private BufferedImage br = ImageIO.read(new File("files/Chess_rdt60.png"));
    private BufferedImage bb = ImageIO.read(new File("files/Chess_bdt60.png"));
    private BufferedImage bn = ImageIO.read(new File("files/Chess_ndt60.png"));
    private BufferedImage bp = ImageIO.read(new File("files/Chess_pdt60.png"));
    public GameBoard() throws IOException {
        setupBoard();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX()>800 && e.getX()<880) {
                    //System.out.println(e.getX());
                    promotingRow=e.getY()/80;
                    //System.out.println("Promoting Row: "+promotingRow);
                } else if(!promoting) {
                    currCol = Math.min(7, e.getX() / 100);
                    currRow = Math.min(7, e.getY() / 100);
                    //System.out.println("Initial Position: " + currCol + " " + currRow);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!promoting) {
                    nextCol = Math.min(7, e.getX() / 100);
                    nextRow = Math.min(7, e.getY() / 100);
                    //System.out.println("Final Position: " + nextCol + " " + nextRow);
                    if(promotingRow>-1){
                        //System.out.println("Trying to place");
                        tryPlace(promotingRow, nextRow, nextCol);
                    } else {
                        tryMove();
                    }
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(promoting) {
                    int nextColPromotion = Math.min(799, e.getX() / 100);
                    int nextRowPromotion = Math.min(799, e.getY() / 100);
                    //System.out.println(e.getX());
                    //System.out.println(currRow+" "+currCol+" "+nextRow+" "+nextCol+" "+nextRowPromotion+" "+nextColPromotion);
                    promote(nextRowPromotion, nextColPromotion);
                }
            }
        });
    }
    public void tryMove() {
        //System.out.println("White's Turn: " + whitesTurn);
        if(!tryCastle()){
            if(board[currRow][currCol]!=null && board[currRow][currCol].getClass().getName().equals("org.example.Pawn")){
                board[currRow][currCol].getEnPassant(whiteCanEnPassant,blackCanEnPassant);
            }
            if (board[currRow][currCol] != null && board[currRow][currCol].validMove(board, nextRow, nextCol) && notStillInCheck(currRow, currCol, nextRow, nextCol)) {
                move(currRow, currCol, nextRow, nextCol);
            }
        }
        this.repaint(0, 0, 800, 800);
    }
    public void move(int currRow, int currCol, int nextRow, int nextCol) {
        Arrays.fill(whiteCanEnPassant, false);
        Arrays.fill(blackCanEnPassant, false);
        if (board[currRow][currCol].getClass().getName().equals("org.example.Pawn")&&(nextRow==0||nextRow==7)){
            for(PromotionListener p : promotionListeners) {
                p.toggleVisibility();
                promoting=true;
            }
        } else {
            if (board[nextRow][nextCol] != null) {
                capture(board[nextRow][nextCol]);
            }
            if(board[currRow][currCol].getClass().getName().equals("org.example.Pawn")){
                board[currRow][currCol].setMoved(true);
            }
            if (board[currRow][currCol].getClass().getName().equals("org.example.Pawn") && Math.abs(nextRow-currRow)==2){
                if(whitesTurn){
                    whiteCanEnPassant[currCol]=true;
                } else {
                    blackCanEnPassant[currCol]=true;
                }
            }
            if (board[currRow][currCol].getClass().getName().equals("org.example.Pawn") && nextCol!=currCol && board[nextRow][nextCol]==null) {
                board[nextRow][nextCol] = board[currRow][currCol];
                board[currRow][currCol] = null;
                capture(board[currRow][nextCol]);
                board[currRow][nextCol]=null;
                board[nextRow][nextCol].setRow(nextRow);
                board[nextRow][nextCol].setColumn(nextCol);

            } else {
//                System.out.println(Arrays.toString(whiteCanEnPassant));
//                System.out.println(Arrays.toString(blackCanEnPassant));
                board[nextRow][nextCol] = board[currRow][currCol];
                board[currRow][currCol] = null;
                board[nextRow][nextCol].setRow(nextRow);
                board[nextRow][nextCol].setColumn(nextCol);
            }
            if (board[nextRow][nextCol].getClass().getName().equals("org.example.King")) {
                if (board[nextRow][nextCol].isWhite()) {
                    whiteKingRow = nextRow;
                    whiteKingCol = nextCol;
                    //System.out.println("White King Location:" + whiteKingRow + " " + whiteKingCol);
                    whiteShortCastle=false;
                    whiteLongCastle=false;
                } else {
                    blackKingRow = nextRow;
                    blackKingCol = nextCol;
                    //System.out.println("Black King Location: " + blackKingRow + " " + blackKingCol);
                    blackShortCastle=false;
                    blackLongCastle=false;
                }
            } else if (currRow==0 && currCol==0) {
                blackLongCastle=false;
            } else if (currRow==0 && currCol==7) {
                blackShortCastle=false;
            } else if (currRow==7 && currCol==0) {
                whiteLongCastle=false;
            } else if (currRow==7 && currCol==7) {
                whiteShortCastle=false;
            }
            whitesTurn = !whitesTurn;
        }
        //System.out.println("White's turn while checking:" + whitesTurn);
        if (!piecesChecking().isEmpty()) {
            //System.out.println("In check: " + piecesChecking().toString());
            //System.out.println("Checking for checkmate");
            if (inCheckMate()) {
                //System.out.println("In Checkmate");
                for(CheckmateListener c :checkmateListeners){
                    c.display();
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    //System.out.print(board[i][j].toString() + " ");
                } else {
                    //System.out.print("  ");
                }
            }
            //System.out.println();
        }
//        for(int i=0; i<10; i++){
//            System.out.print(capturedPieces[i]+" ");
//        }
//        System.out.println();
//        System.out.print("White pieces: ");
//        for(GamePiece p : whitePiecesOnBoard) {
//            System.out.print(p.toString()+" ");
//        }
//        System.out.println();
//        System.out.print("Black pieces: ");
//        for(GamePiece p : blackPiecesOnBoard) {
//            System.out.print(p.toString()+" ");
//        }
//        System.out.println();
//        System.out.println("MOVE END");
//        System.out.println();
    }
    public boolean tryCastle(){
        if (whitesTurn) {
            if(currRow==7&&currCol==4 && nextRow==7 && nextCol==6){
                if(whiteShortCastle && board[7][5]==null&&board[7][6]==null && notStillInCheck(7,4,7,5) && notStillInCheck(7,4,7,6)){
                    board[7][6]=board[7][4];
                    board[7][6].setColumn(6);
                    board[7][5]=board[7][7];
                    board[7][5].setColumn(5);
                    board[7][7]=null;
                    board[7][4]=null;
                    whiteKingCol=6;
                    whiteLongCastle=false;
                    whiteShortCastle=false;
                    whitesTurn=false;
                    //System.out.println("White Short Castled");
                    return true;
                }
            } else if (currRow==7&&currCol==4 && nextRow==7 && nextCol==2) {
                //System.out.println("White tried to Long Castled");
                if(whiteLongCastle && board[7][3]==null && board[7][1]==null&& board[7][2]==null && notStillInCheck(7,4,7,3) && notStillInCheck(7,4,7,2)){
                    board[7][2]=board[7][4];
                    board[7][2].setColumn(2);
                    board[7][3]=board[7][0];
                    board[7][3].setColumn(3);
                    board[7][0]=null;
                    board[7][4]=null;
                    whiteKingCol=2;
                    whiteLongCastle=false;
                    whiteShortCastle=false;
                    whitesTurn=false;
                    //System.out.println("White Long Castled");
                    return true;
                }
            }
        } else {
            if(currRow==0&&currCol==4 && nextRow==0 && nextCol==6){
                if(blackShortCastle && board[0][5]==null&&board[0][6]==null && notStillInCheck(0,4,0,5) && notStillInCheck(0,4,0,6)){
                    board[0][6]=board[0][4];
                    board[0][6].setColumn(6);
                    board[0][5]=board[0][7];
                    board[0][5].setColumn(5);
                    board[0][7]=null;
                    board[0][4]=null;
                    blackKingCol=6;
                    blackLongCastle=false;
                    blackShortCastle=false;
                    whitesTurn=true;
                    //System.out.println("Black Short Castled");
                    return true;
                }
            } else if (currRow==0&&currCol==4 && nextRow==0 && nextCol==2) {
                //System.out.println("White tried to Long Castled");
                if(blackLongCastle && board[0][3]==null && board[0][1]==null&& board[0][2]==null && notStillInCheck(0,4,0,3) && notStillInCheck(0,4,0,2)){
                    board[0][2]=board[7][4];
                    board[0][2].setColumn(2);
                    board[0][3]=board[7][0];
                    board[0][3].setColumn(3);
                    board[0][0]=null;
                    board[0][4]=null;
                    blackKingCol=2;
                    blackLongCastle=false;
                    blackShortCastle=false;
                    whitesTurn=true;
                    //System.out.println("Black Long Castled");
                    return true;
                }
            }
        }
        return false;
    }
    public void promote (int promotingRow, int promotingCol){
        //System.out.println(promotingRow+" "+promotingCol);
        if(promotingRow==0 && promotingCol==0) {
            Queen q = new Queen(nextRow, nextCol, whitesTurn, true, pieceNumber++);
            if(whitesTurn){
                whitePiecesOnBoard.add(q);
            } else {
                blackPiecesOnBoard.add(q);
            }
            if(board[nextRow][nextCol]!=null){
                capture(board[nextRow][nextCol]);
            }
            whitePiecesOnBoard.remove(board[currRow][currCol]);
            board[nextRow][nextCol]=q;
            board[currRow][currCol]=null;
            whitesTurn = !whitesTurn;
        } else if(promotingRow==1 && promotingCol==0) {
            Knight n = new Knight(nextRow, nextCol, whitesTurn, true, pieceNumber++);
            if(whitesTurn){
                whitePiecesOnBoard.add(n);
            } else {
                blackPiecesOnBoard.add(n);
            }
            if(board[nextRow][nextCol]!=null){
                capture(board[nextRow][nextCol]);
            }
            whitePiecesOnBoard.remove(board[currRow][currCol]);
            board[nextRow][nextCol]=n;
            board[currRow][currCol]=null;
            whitesTurn = !whitesTurn;
        } else if(promotingRow==2 && promotingCol==0) {
            Rook r = new Rook(nextRow, nextCol, whitesTurn, true, pieceNumber++);
            if(whitesTurn){
                whitePiecesOnBoard.add(r);
            } else {
                blackPiecesOnBoard.add(r);
            }
            if(board[nextRow][nextCol]!=null){
                capture(board[nextRow][nextCol]);
            }
            whitePiecesOnBoard.remove(board[currRow][currCol]);
            board[nextRow][nextCol]=r;
            board[currRow][currCol]=null;
            whitesTurn = !whitesTurn;
        } else if(promotingRow==3 && promotingCol==0) {
            Bishop b = new Bishop(nextRow, nextCol, whitesTurn, true, pieceNumber++);
            if(whitesTurn){
                whitePiecesOnBoard.add(b);
            } else {
                blackPiecesOnBoard.add(b);
            }
            if(board[nextRow][nextCol]!=null){
                capture(board[nextRow][nextCol]);
            }
            whitePiecesOnBoard.remove(board[currRow][currCol]);
            board[nextRow][nextCol]=b;
            board[currRow][currCol]=null;
            whitesTurn = !whitesTurn;
        }
        promoting=!promoting;
        for(PromotionListener p : promotionListeners){
            p.toggleVisibility();
        }
        this.repaint();
    }
    public void tryPlace (int placingRow, int nextRow, int nextCol) {
        if(board[nextRow][nextCol]==null && capturedPieces[placingRow]>0) {
            if(whitesTurn) {
                if(placingRow==0){
                    Queen q = new Queen(nextRow, nextCol, true, false, pieceNumber++);
                    board[nextRow][nextCol]= q;
                    whitePiecesOnBoard.add(q);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==1){
                    Rook r = new Rook(nextRow, nextCol, true, false, pieceNumber++);
                    board[nextRow][nextCol]= r;
                    whitePiecesOnBoard.add(r);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==2){
                    Bishop b = new Bishop(nextRow, nextCol, true, false, pieceNumber++);
                    board[nextRow][nextCol]= b;
                    whitePiecesOnBoard.add(b);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==3){
                    Knight n = new Knight(nextRow, nextCol, true, false, pieceNumber++);
                    board[nextRow][nextCol]= n;
                    whitePiecesOnBoard.add(n);
                    capturedPieces[placingRow]--;
                } else if(placingRow==4 && nextRow!=0&&nextRow!=7){
                    Pawn p = new Pawn(nextRow, nextCol, true, false, true, pieceNumber++);
                    board[nextRow][nextCol]= p;
                    whitePiecesOnBoard.add(p);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                }
            } else {
                if(placingRow==5){
                    Queen q = new Queen(nextRow, nextCol, false, false, pieceNumber++);
                    board[nextRow][nextCol]= q;
                    whitePiecesOnBoard.add(q);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==6){
                    Rook r = new Rook(nextRow, nextCol, false, false, pieceNumber++);
                    board[nextRow][nextCol]= r;
                    whitePiecesOnBoard.add(r);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==7){
                    Bishop b = new Bishop(nextRow, nextCol, false, false, pieceNumber++);
                    board[nextRow][nextCol]= b;
                    whitePiecesOnBoard.add(b);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==8){
                    Knight n = new Knight(nextRow, nextCol, false, false, pieceNumber++);
                    board[nextRow][nextCol]= n;
                    whitePiecesOnBoard.add(n);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                } else if(placingRow==9 && nextRow!=0&&nextRow!=7){
                    Pawn p = new Pawn(nextRow, nextCol, false, false, true, pieceNumber++);
                    board[nextRow][nextCol]= p;
                    whitePiecesOnBoard.add(p);
                    capturedPieces[placingRow]--;
                    whitesTurn=!whitesTurn;
                }
            }
        }
        promotingRow=-1;
        this.repaint();
    }
    public TreeSet<GamePiece> piecesChecking() {
        TreeSet<GamePiece> piecesChecking = new TreeSet<>();
        whitesTurn = !whitesTurn;
        if (!whitesTurn) {
            for (GamePiece g : blackPiecesOnBoard) {
                if (g.validMove(board, whiteKingRow, whiteKingCol)) {
                    piecesChecking.add(g);
                }
            }
        } else {
            for (GamePiece g : whitePiecesOnBoard) {
                if (g.validMove(board, blackKingRow, blackKingCol)) {
                    piecesChecking.add(g);
                }
            }
        }
        whitesTurn = !whitesTurn;
        return piecesChecking;
    }

    public boolean notStillInCheck(int currRow, int currCol, int nextRow, int nextCol) {
        GamePiece currPiece = board[currRow][currCol];
        GamePiece nextPiece = board[nextRow][nextCol];
        board[nextRow][nextCol] = board[currRow][currCol];
        board[currRow][currCol] = null;
        if (whitesTurn && nextPiece != null) {
            blackPiecesOnBoard.remove(nextPiece);
        } else if (!whitesTurn && nextPiece != null) {
            whitePiecesOnBoard.remove(nextPiece);
        }
        if (currPiece.getClass().getName().equals("org.example.King")) {
            if (currPiece.isWhite()) {
                whiteKingRow = nextRow;
                whiteKingCol = nextCol;
            } else {
                blackKingRow = nextRow;
                blackKingCol = nextCol;
            }
        }
        if (piecesChecking().isEmpty()) {
            board[currRow][currCol] = currPiece;
            board[nextRow][nextCol] = nextPiece;
            if (currPiece.getClass().getName().equals("org.example.King")) {
                if (currPiece.isWhite()) {
                    whiteKingRow = currRow;
                    whiteKingCol = currCol;
                } else {
                    blackKingRow = currRow;
                    blackKingCol = currCol;
                }
            }
            return true;
        }
        //System.out.println("Pieces still checking after move:" + piecesChecking().toString());
        board[currRow][currCol] = currPiece;
        board[nextRow][nextCol] = nextPiece;
        if (whitesTurn && nextPiece != null) {
            blackPiecesOnBoard.add(nextPiece);
        } else if (!whitesTurn && nextPiece != null) {
            whitePiecesOnBoard.add(nextPiece);
        }
        if (currPiece.getClass().getName().equals("org.example.King")) {
            if (currPiece.isWhite()) {
                whiteKingRow = currRow;
                whiteKingCol = currCol;
            } else {
                blackKingRow = currRow;
                blackKingCol = currCol;
            }
        }
        return false;
    }

    public boolean inCheckMate() {
        TreeSet<GamePiece> piecesChecking = piecesChecking();
        if (piecesChecking.isEmpty()) {
            return false;
        }
        if (piecesChecking.size() == 1) {
            GamePiece p = piecesChecking.pollFirst();
            int checkingRow = p.getRow();
            int checkingCol = p.getColumn();
            String pieceName = p.getClass().getName();
            if (canBlock(pieceName, checkingRow, checkingCol)) {
                return false;
            } else if (canCapture(pieceName, checkingRow, checkingCol)) {
                return false;
            } else return !canRun();
        } else if (piecesChecking.size() == 2) {
            return !canRun();
        }
        return true;
    }
    public boolean canBlock(String pieceName, int checkingRow, int checkingColumn) {
        if (pieceName.equals("org.example.Knight") || pieceName.equals("org.example.Pawn")) {
            return false;
        }
        if (whitesTurn) {
            if(Math.abs(whiteKingRow-checkingRow)>1 || Math.abs(whiteKingCol-checkingColumn)>1){
                return capturedPieces[0]+capturedPieces[1]+capturedPieces[2]+capturedPieces[3]+capturedPieces[4]>0;
            }
            if (checkingRow == whiteKingRow) {
                for (int i = Math.min(whiteKingCol, checkingColumn) + 1; i < Math.max(checkingColumn, whiteKingCol); i++) {
                    for (GamePiece p : whitePiecesOnBoard) {
                        if (p.validMove(board, checkingRow, i) && notStillInCheck(p.getRow(), p.getColumn(), checkingRow, i)) {
                            //System.out.println("White Can Block Horizontally");
                            return true;
                        }
                    }
                }
            } else if (checkingColumn == whiteKingCol) {
                for (int i = Math.min(whiteKingRow, checkingRow) + 1; i < Math.max(checkingRow, whiteKingRow); i++) {
                    for (GamePiece p : whitePiecesOnBoard) {
                        if (p.validMove(board, i, checkingColumn) && notStillInCheck(p.getRow(), p.getColumn(), i, checkingColumn)) {
                            //System.out.println("White Can Block Vertically");
                            return true;
                        }
                    }
                }
            } else if ((checkingRow > whiteKingRow && checkingColumn > whiteKingCol) || (checkingRow < whiteKingRow && checkingColumn < whiteKingCol)) {
                for (int i = 1; i < Math.abs(checkingRow - whiteKingRow); i++) {
                    for (GamePiece p : whitePiecesOnBoard) {
                        if (p.validMove(board, Math.min(whiteKingRow, checkingRow) + i, Math.min(whiteKingCol, checkingColumn) - i) &&  notStillInCheck(p.getRow(), p.getColumn(), Math.min(whiteKingRow, checkingRow) + i, Math.min(whiteKingCol, checkingColumn) - i)) {
                            //System.out.println("White Can Block \\");
                            return true;
                        }
                    }
                }
            } else {
                for (int i = 1; i < Math.abs(checkingRow - whiteKingRow); i++) {
                    for (GamePiece p : whitePiecesOnBoard) {
                        if (p.validMove(board, Math.min(whiteKingRow, checkingRow) + i, Math.max(whiteKingCol, checkingColumn) - i) && notStillInCheck(p.getRow(), p.getColumn(), Math.min(whiteKingRow, checkingRow) + i, Math.max(whiteKingCol, checkingColumn) - i)) {
                            //System.out.println("White Can Block //");
                            return true;
                        }
                    }
                }
            }
        } else {
            if(Math.abs(blackKingRow-checkingRow)>1 || Math.abs(blackKingCol-checkingColumn)>1){
                return capturedPieces[5]+capturedPieces[6]+capturedPieces[7]+capturedPieces[8]+capturedPieces[9]>0;
            }
            if (checkingRow == blackKingRow) {
                for (int i = Math.min(blackKingCol, checkingColumn) + 1; i < Math.max(checkingColumn, blackKingCol); i++) {
                    for (GamePiece p : blackPiecesOnBoard) {
                        if (p.validMove(board, checkingRow, i) && notStillInCheck(p.getRow(), p.getColumn(), checkingRow, i)&&  notStillInCheck(p.getRow(), p.getColumn(), Math.min(blackKingRow, checkingRow) + i, Math.min(blackKingCol, checkingColumn) - i)) {
                            //System.out.println("Black Can Block Horizontally");
                            return true;
                        }
                    }
                }
            } else if (checkingColumn == blackKingCol) {
                for (int i = Math.min(blackKingRow, checkingRow) + 1; i < Math.max(checkingRow, blackKingRow); i++) {
                    for (GamePiece p : blackPiecesOnBoard) {
                        if (p.validMove(board, i, checkingColumn) && notStillInCheck(p.getRow(), p.getColumn(), i, checkingColumn)) {
                            //System.out.println("Black Can Block Vertically");
                            return true;
                        }
                    }
                }
            } else if ((checkingRow > blackKingRow && checkingColumn > blackKingCol) || (checkingRow < blackKingRow && checkingColumn < blackKingCol)) {
                for (int i = 1; i < Math.abs(checkingRow - blackKingRow); i++) {
                    for (GamePiece p : blackPiecesOnBoard) {
                        if (p.validMove(board, Math.min(blackKingRow, checkingRow) + i, Math.min(blackKingCol, checkingColumn) - i)) {
                            //System.out.println("Black Can Block \\");
                            return true;
                        }
                    }
                }
            } else {
                for (int i = 1; i < Math.abs(checkingRow - blackKingRow); i++) {
                    for (GamePiece p : blackPiecesOnBoard) {
                        if (p.validMove(board, Math.min(blackKingRow, checkingRow) + i, Math.max(blackKingCol, checkingColumn) - i) && notStillInCheck(p.getRow(), p.getColumn(), Math.min(blackKingRow, checkingRow) + i, Math.max(blackKingCol, checkingColumn) - i)) {
                            //System.out.println("Black Can Block //");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean canCapture(String pieceName, int checkingRow, int checkingColumn) {
        if(whitesTurn) {
            for(GamePiece p : whitePiecesOnBoard) {
                if(p.validMove(board, checkingRow, checkingColumn) && notStillInCheck(p.getRow(), p.getColumn(), checkingRow, checkingColumn)) {
                    //System.out.println("Can Capture");
                    return true;
                }
            }
        } else {
            for(GamePiece p : blackPiecesOnBoard) {
                if(p.validMove(board, checkingRow, checkingColumn) && notStillInCheck(p.getRow(), p.getColumn(), checkingRow, checkingColumn)) {
                    //System.out.println("Can Capture");
                    return true;
                }
            }
        }
        return false;
    }
    public boolean canRun() {
        if (!whitesTurn) {
            for(int i = Math.max(whiteKingRow-1,0); i<= Math.min(whiteKingRow+1,7); i++) {
                for(int j = Math.max(whiteKingCol-1,0); j<= Math.min(whiteKingCol+1,7); j++) {
                    if ((board[i][j]!=null && !board[i][j].isWhite()) || notStillInCheck(whiteKingRow, whiteKingCol, i, j)) {
                        //System.out.println("Can Run");
                        return true;
                    }
                }
            }
        } else {
            for(int i = Math.max(blackKingRow-1,0); i<= Math.min(blackKingRow+1,7); i++) {
                for(int j = Math.max(blackKingCol-1,0); j<= Math.min(blackKingCol+1,7); j++) {
                    if ((board[i][j]!=null && board[i][j].isWhite()) || notStillInCheck(blackKingRow, blackKingCol, i, j)) {
                        //System.out.println("Can Run");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void capture(GamePiece p) {
        if(p.isWhite()) {
            whitePiecesOnBoard.remove(p);
            if(p.getClass().getName().equals("org.example.Pawn") || p.isPromoted()) {
                capturedPieces[9]++;
            } else if(p.getClass().getName().equals("org.example.Knight")) {
                capturedPieces[8]++;
            } else if(p.getClass().getName().equals("org.example.Bishop")) {
                capturedPieces[7]++;
            } else if(p.getClass().getName().equals("org.example.Rook")) {
                capturedPieces[6]++;
            } else if(p.getClass().getName().equals("org.example.Queen")) {
                capturedPieces[5]++;
            }
        } else {
            blackPiecesOnBoard.remove(p);
            if(p.getClass().getName().equals("org.example.Pawn") || p.isPromoted()) {
                capturedPieces[4]++;
            } else if(p.getClass().getName().equals("org.example.Knight")) {
                capturedPieces[3]++;
            } else if(p.getClass().getName().equals("org.example.Bishop")) {
                capturedPieces[2]++;
            } else if(p.getClass().getName().equals("org.example.Rook")) {
                capturedPieces[1]++;
            } else if(p.getClass().getName().equals("org.example.Queen")) {
                capturedPieces[0]++;
            }
        }
    }
    public void setupBoard(){
        board[0][0] = new Rook(0, 0, false, false, pieceNumber++);
        board[0][1] = new Knight(0, 1, false, false, pieceNumber++);
        board[0][2] = new Bishop(0, 2, false, false, pieceNumber++);
        board[0][3] = new Queen(0, 3, false, false, pieceNumber++);
        board[0][4] = new King(0, 4, false, false, pieceNumber++);
        board[0][5] = new Bishop(0, 5, false, false, pieceNumber++);
        board[0][6] = new Knight(0, 6, false, false, pieceNumber++);
        board[0][7] = new Rook(0, 7, false, false, pieceNumber++);
        for(int i=0; i<8; i++){
            board[1][i] = new Pawn(1, i, false, false, false, pieceNumber++);
        }
        board[7][0] = new Rook(7, 0, true, false, pieceNumber++);
        board[7][1] = new Knight(7, 1, true, false, pieceNumber++);
        board[7][2] = new Bishop(7, 2, true, false, pieceNumber++);
        board[7][3] = new Queen(7, 3, true, false, pieceNumber++);
        board[7][4] = new King(7, 4, true, false, pieceNumber++);
        board[7][5] = new Bishop(7, 5, true, false, pieceNumber++);
        board[7][6] = new Knight(7, 6, true, false, pieceNumber++);
        board[7][7] = new Rook(7, 7, true, false, pieceNumber++);
        for(int i=0; i<8; i++){
            board[6][i] = new Pawn(6, i, true, false, false, pieceNumber++);
        }
        for(int i=0; i<2; i++){
            for(int j=0; j<8; j++){
                blackPiecesOnBoard.add(board[i][j]);
            }
        }
        for(int i=6; i<8; i++){
            for(int j=0; j<8; j++){
                whitePiecesOnBoard.add(board[i][j]);
            }
        }
        whiteShortCastle=true;
        whiteLongCastle=true;
        blackShortCastle=true;
        blackLongCastle=true;
        whiteKingRow=7;
        whiteKingCol=4;
        blackKingRow=0;
        blackKingCol=4;
    }

    public void paintComponent (Graphics gc) {
        gc.setColor(Color.black);
        for (int i=0; i<9; i++){
            gc.drawLine(squareWidth*i, 0, squareWidth*i, 8*squareWidth);
        }
        for (int i=0; i<9; i++){
            gc.drawLine(0,squareWidth*i,   8*squareWidth,squareWidth*i);
        }
        gc.drawLine(880, 0, 880, 800);
        gc.drawLine(960, 0, 960, 800);
        for(int i=0; i<11; i++){
            gc.drawLine(800, i*80, 960, i*80);
        }
        gc.drawImage(wq,800,0,80,80,null);
        gc.drawImage(wr,800,80,80,80,null);
        gc.drawImage(wb,800,160,80,80,null);
        gc.drawImage(wn,800,240,80,80,null);
        gc.drawImage(wp,800,320,80,80,null);
        gc.drawImage(bq,800,400,80,80,null);
        gc.drawImage(br,800,480,80,80,null);
        gc.drawImage(bb,800,560,80,80,null);
        gc.drawImage(bn,800,640,80,80,null);
        gc.drawImage(bp,800,720,80,80,null);
        for(int i=0; i<10; i++){
            gc.drawString(Integer.toString(capturedPieces[i]),920, 80*i+45);
        }
        gc.setColor(new Color(20, 90,50));
        for (int i=0; i<8; i++) {
            for (int j =0; j<8; j++) {
                if ((i+j)%2 == 1) {
                    gc.fillRect(squareWidth*i,squareWidth*j, squareWidth, squareWidth);
                }
            }
        }

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j]!=null){
                    try {
                        board[i][j].repaint(gc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        for(PromotionListener p : promotionListeners){
            p.paintComponent(gc);
        }
        for(CheckmateListener c : checkmateListeners){
            c.paintComponent(gc);
        }
    }
    public static boolean isWhitesTurn(){return whitesTurn;}
    public Dimension getPreferredSize () {
        return new Dimension(8*squareWidth+170, 8*squareWidth+30);
    }
    public void addPromotionListener(PromotionListener p){
        promotionListeners.add(p);
    }
    public void addCheckmateListener(CheckmateListener a) {
        checkmateListeners.add(a);
    }
    public boolean[] getWhiteEnPassant(){
        return whiteCanEnPassant;
    }
    public boolean[] getBlackEnPassant(){
        return blackCanEnPassant;
    }
}