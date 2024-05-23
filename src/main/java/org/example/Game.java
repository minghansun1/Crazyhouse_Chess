package org.example;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// Contains a main method that starts the game. Sets up a new window and a GameBoard.
public class Game {
    public static void main (String[] args) {
        SwingUtilities.invokeLater(new RunGame());
    }
}
