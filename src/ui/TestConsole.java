package ui;

import model.*;
import java.util.Arrays;

public class TestConsole {

    public static void main(String[] args) {
        // 1. Créer une partie en mode simple avec 3 joueurs
        Partie partie = new Partie(
                Arrays.asList("Alice", "Bob", "Charlie"),
                ModeJeu.SIMPLE
        );

        // 2. Initialiser (distribution)
        partie.initialiserPartie();

        // 3. Afficher la distribution
        System.out.println("=== Distribution initiale ===");
        for (Joueur j : partie.getJoueurs()) {
            System.out.println(j.getNom() + " : " + j.getMain().size() + " cartes");
        }
        System.out.println("Centre : " + partie.getCartesCentre().size() + " cartes");

        // 4. Simuler quelques révélations
        System.out.println("\n=== Tour du joueur courant ===");
        Joueur courant = partie.getJoueurCourant();
        System.out.println("Joueur courant : " + courant.getNom());

        // Exemple : révéler une carte du centre
        System.out.println("Révéler une carte du centre :");
        Carte c1 = partie.revelerDepuisCentre();
        System.out.println("Carte révélée : " + c1);

        // Exemple : révéler la plus petite carte de la main du joueur courant
        System.out.println("Révéler la plus petite carte du joueur courant :");
        Carte c2 = partie.revelerDepuisMain(courant, true);
        System.out.println("Carte révélée : " + c2);

        // Exemple : fin de tour (sans forcément former un trio)
        partie.traiterFinDeTour();

        System.out.println("\nCartes révélées (doit être vide) : " + partie.getCartesReveleesDansTour().size());
        System.out.println("État de la partie : " + partie.getEtatPartie());
    }
}
