package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PromotionButton extends JButton implements PromotionListener {
    private int column;
    private boolean visible;
    private int piece=-1;

    public PromotionButton (int c, boolean v){
        column = c;
        visible = v;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                column = e.getY();
                if(GameBoard.isWhitesTurn()){
                    if (column < 100) {
                        piece = 1;
                    } else if (column < 200) {
                        piece = 2;
                    } else if (column < 300) {
                        piece = 3;
                    } else if (column < 400) {
                        piece = 4;
                    } else if (column < 450) {
                        piece =0;
                    }
                } else {
                    if (column < 50) {
                        piece =0;
                    } else if (column < 150) {
                        piece = 4;
                    } else if (column < 250) {
                        piece = 3;
                    } else if (column < 350) {
                        piece = 2;
                    } else if (column < 450) {
                        piece = 1;
                    }
                }
                visible = false;
            }
        });
    }
    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        try {
            if(visible){
                this.setVisible(true);
                this.setEnabled(true);
                gc.setColor(new Color(255,255,255));
                gc.fillRect(0,0,100,450);
                BufferedImage image = ImageIO.read(new File("files/WhitePromotion.jpg"));
                gc.drawImage(image, 0, 0, 100, 450, null);
            } else {
                this.setVisible(false);
                this.setEnabled(false);
            }
        } catch (IOException e){
            System.out.println("Image doesn't exist");
        }
    }
    public void pawnPromoted(int col, boolean isWhite){
        System.out.println("Promoted");
        visible = true;
        System.out.println(isWhite+" "+col);
        this.setBounds(col*100, 0, this.getPreferredSize().width, this.getPreferredSize().height);
        this.repaint();
    }
    @Override
    public Dimension getPreferredSize () {
        return new Dimension(100,450);
    }
    public void toggleVisibility(){
        visible=!visible;
    }
    public int getPromotedPiece(){
        return piece;
    }
}