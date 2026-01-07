package ui;

import model.*;

import java.util.Arrays;
import java.util.Scanner;

public class TestConsoleTourComplet {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Création de la partie en mode simple avec 3 joueurs
        Partie partie = new Partie(
                Arrays.asList("Alice", "Bob", "Charlie"),
                ModeJeu.SIMPLE
        );

        // 2. Initialisation (distribution)
        partie.initialiserPartie();

        System.out.println("=== Début de la partie (mode simple) ===");
        afficherEtatGlobal(partie);

        // 3. Boucle de jeu : quelques tours pour tester
        while (partie.getEtatPartie() == EtatPartie.EN_COURS) {
            Joueur courant = partie.getJoueurCourant();
            System.out.println("\n=== Tour de " + courant.getNom() + " ===");
            System.out.println("*******Les autres joueurs, veillez ne pas regarder l'ecran !!!**************");
            afficherEtatJoueurCourant(partie);

            boolean tourEnCours = true;
            while (tourEnCours && partie.getEtatPartie() == EtatPartie.EN_COURS) {
                System.out.println("\nActions possibles :");
                System.out.println(" 1 - Révéler une carte au centre");
                System.out.println(" 2 - Révéler la plus petite carte d'un joueur");
                System.out.println(" 3 - Révéler la plus grande carte d'un joueur");
                System.out.println(" 4 - Fin du tour");
                System.out.print("Votre choix : ");

                int choix = lireEntier(scanner);

                switch (choix) {
                    case 1 -> { // centre
                        Carte c = partie.revelerDepuisCentre();
                        if (c == null) {
                            System.out.println("Aucune carte cachée disponible au centre.");
                        } else {
                            System.out.println("Carte révélée au centre : " + c);
                        }
                    }
                    case 2, 3 -> { // main d'un joueur
                        boolean plusPetit = (choix == 2);
                        Joueur cible = choisirJoueur(scanner, partie);
                        if (cible == null) {
                            System.out.println("Choix de joueur invalide.");
                        } else {
                            Carte c = partie.revelerDepuisMain(cible, plusPetit);
                            if (c == null) {
                                System.out.println("Aucune carte dans la main de " + cible.getNom());
                            } else {
                                System.out.println("Carte révélée dans la main de " + cible.getNom() + " : " + c);
                            }
                        }
                    }
                    case 4 -> { // fin de tour
                        tourEnCours = false;
                    }
                    default -> System.out.println("Choix invalide.");
                }

                System.out.println("Cartes révélées ce tour : " + partie.getCartesReveleesDansTour().size());

                // Si déjà 3 cartes révélées, on force fin de tour
                if (partie.getCartesReveleesDansTour().size() == 3) {
                    System.out.println("3 cartes révélées : fin automatique du tour.");
                    tourEnCours = false;
                }
            }

            // 4. Fin de tour : traitement du trio éventuel ou remise des cartes
            partie.traiterFinDeTour();

            // 5. Vérifier la victoire
            Joueur gagnant = partie.verifierConditionsVictoire();
            if (gagnant != null) {
                System.out.println("\n*** " + gagnant.getNom() + " a gagné la partie ! ***");
                afficherEtatGlobal(partie);
                break;
            }

            // 6. Passer au joueur suivant
            partie.passerAuJoueurSuivant();
            afficherEtatGlobal(partie);

        }

        scanner.close();
    }

    private static int lireEntier(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Entrez un nombre : ");
        }
        return scanner.nextInt();
    }

    private static Joueur choisirJoueur(Scanner scanner, Partie partie) {
        System.out.println("Choisir un joueur :");
        int index = 1;
        for (Joueur j : partie.getJoueurs()) {
            System.out.println(" " + index + " - " + j.getNom());
            index++;
        }
        System.out.print("Numéro du joueur : ");
        int choix = lireEntier(scanner);
        if (choix < 1 || choix > partie.getJoueurs().size()) {
            return null;
        }
        return partie.getJoueurs().get(choix - 1);
    }

    private static void afficherEtatGlobal(Partie partie) {
        System.out.println("\n--- État global ---");
        for (Joueur j : partie.getJoueurs()) {
            System.out.println(j.getNom()
                    + " | cartes en main : " + j.getMain().size()
                    + " | trios : " + j.getTriosGagnes().size());
        }
        System.out.println("Centre : " + partie.getCartesCentre().size() + " cartes");
        System.out.println("Joueur courant : " + partie.getJoueurCourant().getNom());
    }

    private static void afficherEtatJoueurCourant(Partie partie) {
        Joueur j = partie.getJoueurCourant();
        System.out.println("Main de " + j.getNom() + " (" + j.getMain().size() + " cartes) :");
        System.out.println(j.getMain());
        System.out.println("Cartes au centre : " + partie.getCartesCentre().size());
    }
}
