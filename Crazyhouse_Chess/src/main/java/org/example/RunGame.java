package org.example;

import javax.swing.*;
import java.io.IOException;

public class RunGame implements Runnable{
    @Override
    public void run() {
        JFrame frame = new JFrame("Game");
        JLayeredPane layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        GameBoard g = null;
        try {
            g = new GameBoard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g.setOpaque(false);
        g.setBounds(0, 0, g.getPreferredSize().width, g.getPreferredSize().height);
        PromotionButton p = new PromotionButton(0, false);
        g.addPromotionListener(p);
        CheckmateButton cb = new CheckmateButton();
        cb.setBounds(200, 200, cb.getPreferredSize().width, cb.getPreferredSize().height);
        cb.setVisible(false);
        g.addCheckmateListener(cb);
        layeredPane.add(g, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(p,JLayeredPane.DEFAULT_LAYER+1);
        layeredPane.add(cb, JLayeredPane.DEFAULT_LAYER+2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(g.getPreferredSize()); // Set the size of the frame based on GameBoard's preferred size
        frame.setVisible(true);
    }
}
