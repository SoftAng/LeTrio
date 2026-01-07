package ui;

import model.*;

import javax.swing.*;
import java.awt.*;

public class TrioFrame extends JFrame {

    private final Partie partie;

    // Affichage joueur courant / main / log
    private final JLabel lblJoueurCourant = new JLabel();
    private final JTextArea txtMain = new JTextArea();
    private final JTextArea txtLog = new JTextArea();

    // Centre : liste cliquable
    private final DefaultListModel<String> centreModel = new DefaultListModel<>();
    private final JList<String> lstCentre = new JList<>(centreModel);

    // Sélection de la cible (centre / joueur)
    private final JComboBox<String> comboCible = new JComboBox<>();

    // Boutons d’action
    private final JButton btnRevelerCentre = new JButton("Révéler centre");
    private final JButton btnRevelerPlusPetit = new JButton("Révéler plus petit");
    private final JButton btnRevelerPlusGrand = new JButton("Révéler plus grand");
    private final JButton btnFinTour = new JButton("Fin de tour");

    public TrioFrame(Partie partie) {
        this.partie = partie;
        initUI();
        rafraichirAffichage();
    }

    private void initUI() {
        setTitle("Trio - Mode simple (UTBM)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Zones non éditables
        txtMain.setEditable(false);
        txtLog.setEditable(false);

        txtMain.setBorder(BorderFactory.createTitledBorder("Main du joueur courant"));
        lstCentre.setBorder(BorderFactory.createTitledBorder("Cartes au centre"));
        txtLog.setBorder(BorderFactory.createTitledBorder("Journal"));

        // Remplir la combo cible
        comboCible.addItem("Centre");
        for (Joueur j : partie.getJoueurs()) {
            comboCible.addItem("Joueur : " + j.getNom());
        }

        // Ligne du haut : joueur courant + choix de cible
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(lblJoueurCourant, BorderLayout.WEST);
        panelTop.add(comboCible, BorderLayout.EAST);

        // Milieu : main / centre
        JPanel panelMiddle = new JPanel(new GridLayout(1, 2));
        panelMiddle.add(new JScrollPane(txtMain));
        panelMiddle.add(new JScrollPane(lstCentre));

        // Boutons d’action
        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnRevelerCentre);
        panelButtons.add(btnRevelerPlusPetit);
        panelButtons.add(btnRevelerPlusGrand);
        panelButtons.add(btnFinTour);

        // Bas : boutons + log
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(panelButtons, BorderLayout.NORTH);
        panelBottom.add(new JScrollPane(txtLog), BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);
        getContentPane().add(panelMiddle, BorderLayout.CENTER);
        getContentPane().add(panelBottom, BorderLayout.SOUTH);

        // Listeners
        comboCible.addActionListener(e -> mettreAJourBoutonsSelonCible());

        btnRevelerCentre.addActionListener(e -> revelerCentre());
        btnRevelerPlusPetit.addActionListener(e -> revelerPlusPetit());
        btnRevelerPlusGrand.addActionListener(e -> revelerPlusGrand());
        btnFinTour.addActionListener(e -> finDeTour());

        mettreAJourBoutonsSelonCible();
    }

    // --- Affichage ---

    private void rafraichirAffichage() {
        Joueur j = partie.getJoueurCourant();
        lblJoueurCourant.setText("Joueur courant : " + j.getNom()
                + " | Trios : " + j.getTriosGagnes().size());

        // main du joueur courant
        StringBuilder sbMain = new StringBuilder();
        j.getMain().forEach(c ->
                sbMain.append(c.getNumero())
                        .append(" - ")
                        .append(c.getNom())
                        .append(c.isVisible() ? " (visible)" : "")
                        .append("\n")
        );
        txtMain.setText(sbMain.toString());

        // cartes au centre : détail seulement si visible
        centreModel.clear();
        partie.getCartesCentre().forEach(c -> {
            if (c.isVisible()) {
                centreModel.addElement(
                        c.getNumero() + " - " + c.getNom() + " (visible)"
                );
            } else {
                centreModel.addElement("[carte cachée]");
            }
        });

        if (partie.getEtatPartie() == EtatPartie.TERMINEE) {
            setBoutonsActifs(false);
        }
    }

    private void log(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    // --- Gestion de la cible / boutons ---

    private void mettreAJourBoutonsSelonCible() {
        String sel = (String) comboCible.getSelectedItem();
        if (sel == null) return;

        boolean centre = sel.equals("Centre");

        // Si centre sélectionné : seul "Révéler centre" est pertinent
        btnRevelerCentre.setEnabled(centre);
        btnRevelerPlusPetit.setEnabled(!centre);
        btnRevelerPlusGrand.setEnabled(!centre);
    }

    private Joueur getJoueurSelectionne() {
        String sel = (String) comboCible.getSelectedItem();
        if (sel == null || !sel.startsWith("Joueur : ")) {
            return null;
        }
        String nom = sel.substring("Joueur : ".length());
        return partie.getJoueurs().stream()
                .filter(j -> j.getNom().equals(nom))
                .findFirst()
                .orElse(null);
    }

    private void setBoutonsActifs(boolean actif) {
        btnRevelerCentre.setEnabled(actif);
        btnRevelerPlusPetit.setEnabled(actif);
        btnRevelerPlusGrand.setEnabled(actif);
        btnFinTour.setEnabled(actif);
        comboCible.setEnabled(actif);
        lstCentre.setEnabled(actif);
    }

    // --- Actions boutons ---

    private void revelerCentre() {
        String sel = (String) comboCible.getSelectedItem();
        if (sel == null || !sel.equals("Centre")) {
            log("Pour révéler au centre, sélectionnez 'Centre' dans la liste.");
            return;
        }

        int index = lstCentre.getSelectedIndex();
        if (index == -1) {
            log("Sélectionnez d'abord une carte au centre (ligne de la liste).");
            return;
        }

        Carte c = partie.revelerDepuisCentre(index);
        if (c == null) {
            log("Cette carte est déjà visible ou invalide.");
        } else {
            log("Révélé au centre : " + c.getNumero() + " - " + c.getNom());
        }
        verifierFinAutoDuTour();
        rafraichirAffichage();
    }

    private void revelerPlusPetit() {
        Joueur cible = getJoueurSelectionne();
        if (cible == null) {
            log("Pour révéler une main, sélectionnez 'Joueur : ...' dans la liste.");
            return;
        }
        Carte c = partie.revelerDepuisMain(cible, true);
        if (c == null) {
            log("Le joueur " + cible.getNom() + " n'a plus de cartes.");
        } else {
            log("Révélé (plus petit) dans la main de "
                    + cible.getNom() + " : "
                    + c.getNumero() + " - " + c.getNom());
        }
        verifierFinAutoDuTour();
        rafraichirAffichage();
    }

    private void revelerPlusGrand() {
        Joueur cible = getJoueurSelectionne();
        if (cible == null) {
            log("Pour révéler une main, sélectionnez 'Joueur : ...' dans la liste.");
            return;
        }
        Carte c = partie.revelerDepuisMain(cible, false);
        if (c == null) {
            log("Le joueur " + cible.getNom() + " n'a plus de cartes.");
        } else {
            log("Révélé (plus grand) dans la main de "
                    + cible.getNom() + " : "
                    + c.getNumero() + " - " + c.getNom());
        }
        verifierFinAutoDuTour();
        rafraichirAffichage();
    }

    private void verifierFinAutoDuTour() {
        if (partie.getCartesReveleesDansTour().size() == 3) {
            log("3 cartes révélées : fin automatique du tour.");
            finDeTour();
        }
    }

    private void finDeTour() {
        partie.traiterFinDeTour();

        var gagnant = partie.verifierConditionsVictoire();
        if (gagnant != null) {
            log("Victoire de " + gagnant.getNom() + " !");
            JOptionPane.showMessageDialog(
                    this,
                    "Le joueur " + gagnant.getNom() + " a gagné la partie !",
                    "Victoire",
                    JOptionPane.INFORMATION_MESSAGE
            );
            setBoutonsActifs(false);
        } else {
            partie.passerAuJoueurSuivant();
            log("Tour terminé. Prochain joueur : " + partie.getJoueurCourant().getNom());
            rafraichirAffichage();
        }
    }
}

