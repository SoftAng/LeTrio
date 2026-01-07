package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Partie {

    private final List<Joueur> joueurs;
    private final List<Carte> cartesCentre;
    private Joueur joueurCourant;
    private final List<Carte> cartesReveleesDansTour;
    private final ModeJeu modeJeu;
    private EtatPartie etatPartie;

    private final Random random = new Random();

    public Partie(List<String> nomsJoueurs, ModeJeu modeJeu) {
        if (nomsJoueurs == null || nomsJoueurs.size() < 3 || nomsJoueurs.size() > 6) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être entre 3 et 6.");
        }
        this.modeJeu = modeJeu;
        this.joueurs = new ArrayList<>();
        int id = 1;
        for (String nom : nomsJoueurs) {
            this.joueurs.add(new Joueur(id++, nom));
        }
        this.cartesCentre = new ArrayList<>();
        this.cartesReveleesDansTour = new ArrayList<>();
        this.etatPartie = EtatPartie.EN_COURS;
        this.joueurCourant = joueurs.get(0);
    }

    // --- Initialisation et distribution ---

    public void initialiserPartie() {
        List<Carte> toutesCartes = new ArrayList<>();
        int idCarte = 1;

        // le nom des cartes dans notre adaptation utbm
        String[] nomsCartes = {
            "Cours",          // 1
            "TP",             // 2
            "TD",             // 3
            "TE",             // 4
            "Projets",        // 5
            "Rapports",       // 6
            "Exam finals",    // 7
            "Stage",          // 8
            "Soutenance",     // 9
            "Atelier",        // 10
            "Partiel",        // 11
            "Mémoire"         // 12
        };

        for (int numero = 1; numero <= 12; numero++) {
            String nom = nomsCartes[numero - 1];
            for (int i = 0; i < 3; i++) {
                toutesCartes.add(new Carte(idCarte++, numero, nom));
            }
        }

        Collections.shuffle(toutesCartes, random);

        int nbJoueurs = joueurs.size();
        int cartesParJoueur;
        int cartesCentreCount;

        // Tableau officiel des règles pour le mode individuel simple. [file:12]
        switch (nbJoueurs) {
            case 3 -> {
                cartesParJoueur = 9;
                cartesCentreCount = 9;
            }
            case 4 -> {
                cartesParJoueur = 7;
                cartesCentreCount = 8;
            }
            case 5 -> {
                cartesParJoueur = 6;
                cartesCentreCount = 6;
            }
            case 6 -> {
                cartesParJoueur = 5;
                cartesCentreCount = 6;
            }
            default -> throw new IllegalStateException("Nombre de joueurs invalide.");
        }

        int index = 0;
        // distribuer aux joueurs
        for (Joueur joueur : joueurs) {
            for (int i = 0; i < cartesParJoueur; i++) {
                joueur.ajouterCarte(toutesCartes.get(index++));
            }
        }

        // cartes au centre
        for (int i = 0; i < cartesCentreCount; i++) {
            cartesCentre.add(toutesCartes.get(index++));
        }

        for (Joueur joueur : joueurs) {
            joueur.trierMain();
        }
        cartesReveleesDansTour.clear();
    }

    // --- Getters de base ---

    public List<Joueur> getJoueurs() {
        return Collections.unmodifiableList(joueurs);
    }

    public List<Carte> getCartesCentre() {
        return Collections.unmodifiableList(cartesCentre);
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public EtatPartie getEtatPartie() {
        return etatPartie;
    }

    public List<Carte> getCartesReveleesDansTour() {
        return Collections.unmodifiableList(cartesReveleesDansTour);
    }

    // --- Actions de jeu ---

    /**
     * Révèle une carte au centre.
     * Dans la version GUI, on pourra choisir l’index précisément.
     * Ici, on révèle juste la première carte encore cachée.
     */
    public Carte revelerDepuisCentre() {
        for (Carte carte : cartesCentre) {
            if (!carte.isVisible()) {
                carte.setVisible(true);
                ajouterCarteRevelee(carte);
                return carte;
            }
        }
        return null; // plus de cartes à révéler au centre
    }
    
    public Carte revelerDepuisCentre(int index) {
        if (index < 0 || index >= cartesCentre.size()) {
            return null;
        }
        Carte carte = cartesCentre.get(index);
        if (carte.isVisible()) {
            return null; // déjà révélée
        }
        carte.setVisible(true);
        ajouterCarteRevelee(carte);
        return carte;
    }

    /**
     * Révèle la plus petite ou la plus grande carte d’un joueur cible.
     * @param cible     joueur dont on révèle une carte
     * @param plusPetit true -> plus petit numéro, false -> plus grand numéro
     */
    public Carte revelerDepuisMain(Joueur cible, boolean plusPetit) {
        List<Carte> main = cible.getMain();
        if (main.isEmpty()) {
            return null;
        }

        Carte carte;
        if (plusPetit) {
            carte = main.get(0);
        } else {
            carte = main.get(main.size() - 1);
        }

        carte.setVisible(true);
        ajouterCarteRevelee(carte);
        return carte;
    }

    private void ajouterCarteRevelee(Carte carte) {
        cartesReveleesDansTour.add(carte);
    }

    /**
     * À appeler quand le tour se termine :
     * - soit parce qu’un numéro différent est apparu,
     * - soit après avoir tenté de former un trio.
     *
     * Si un trio complet (3 cartes même numéro) a été révélé, on le forme
     * et on retire les cartes du jeu, sinon on remet les cartes face cachée.
     */
    public void traiterFinDeTour() {
        if (cartesReveleesDansTour.isEmpty()) {
            return;
        }

        if (cartesReveleesDansTour.size() == 3 &&
                toutesMemes(cartesReveleesDansTour)) {
            formerTrioCourant();
        } else {
            // pas de trio valide : on remet tout face cachée
            remettreCartesReveleesFaceCachee();
        }
        cartesReveleesDansTour.clear();
    }

    private boolean toutesMemes(List<Carte> cartes) {
        int num = cartes.get(0).getNumero();
        return cartes.stream().allMatch(c -> c.getNumero() == num);
    }

    /**
     * Crée un Trio pour le joueur courant, retire les cartes
     * des mains / du centre, et les laisse visibles dans le trio.
     */
    private void formerTrioCourant() {
        int numero = cartesReveleesDansTour.get(0).getNumero();
        Trio trio = new Trio(genererIdTrio(), numero,
                new ArrayList<>(cartesReveleesDansTour));
        joueurCourant.ajouterTrio(trio);

        // Retirer les cartes du centre et des mains
        for (Carte carte : cartesReveleesDansTour) {
            cartesCentre.remove(carte);
            for (Joueur j : joueurs) {
                j.retirerCarte(carte);
            }
        }
    }

    private void remettreCartesReveleesFaceCachee() {
        for (Carte carte : cartesReveleesDansTour) {
            carte.setVisible(false);
        }
    }

    private int genererIdTrio() {
        int count = 0;
        for (Joueur j : joueurs) {
            count += j.getTriosGagnes().size();
        }
        return count + 1;
    }

    /**
     * Vérifie les conditions de victoire du mode simple :
     * - 3 trios, ou
     * - le trio de 7.
     * Renvoie le gagnant, ou null si la partie continue.
     */
    public Joueur verifierConditionsVictoire() {
        for (Joueur j : joueurs) {
            if (j.getNombreTrios() >= 3 || j.possedeTrioDe7()) {
                etatPartie = EtatPartie.TERMINEE;
                return j;
            }
        }
        return null;
    }

    /**
     * Passe au joueur suivant (tour par tour).
     */
    public void passerAuJoueurSuivant() {
        int index = joueurs.indexOf(joueurCourant);
        index = (index + 1) % joueurs.size();
        joueurCourant = joueurs.get(index);
        cartesReveleesDansTour.clear();
    }
}
