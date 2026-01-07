package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Joueur {

    private final int id;
    private final String nom;
    private final List<Carte> main;
    private final List<Trio> triosGagnes;

    public Joueur(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.main = new ArrayList<>();
        this.triosGagnes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public List<Carte> getMain() {
        return Collections.unmodifiableList(main);
    }

    public List<Trio> getTriosGagnes() {
        return Collections.unmodifiableList(triosGagnes);
    }

    public void ajouterCarte(Carte carte) {
        if (carte != null) {
            main.add(carte);
            trierMain();
        }
    }

    public void trierMain() {
        main.sort(Comparator.comparingInt(Carte::getNumero));
    }

    public boolean retirerCarte(Carte carte) {
        return main.remove(carte);
    }

    public void ajouterTrio(Trio trio) {
        if (trio != null) {
            triosGagnes.add(trio);
        }
    }

    public int getNombreTrios() {
        return triosGagnes.size();
    }

    public boolean possedeTrioDe7() {
        return triosGagnes.stream().anyMatch(t -> t.getNumero() == 7);
    }
}
