package ui;


import model.*;

import javax.swing.*;
import java.util.Arrays;

public class TrioApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Exemples de noms de joueurs
            var noms = Arrays.asList("Alice", "Bob", "Charlie");

            Partie partie = new Partie(noms, ModeJeu.SIMPLE);
            partie.initialiserPartie();

            TrioFrame frame = new TrioFrame(partie);
            frame.setVisible(true);
        });
    }
}
