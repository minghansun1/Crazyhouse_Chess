package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckmateButton extends JButton implements CheckmateListener{
    boolean visible;
    public CheckmateButton(){
        super("Checkmate");
        visible=false;
        addMouseListener(new MouseAdapter() {

        });
    }
    @Override
    public void paintComponent(Graphics gc) {
        if(visible){
            super.paintComponent(gc);
        }
    }
    public void display() {
        System.out.println("anoe");
        visible=true;
        this.repaint();
    }
    public Dimension getPreferredSize () {
        return new Dimension(300,100);
    }
}
