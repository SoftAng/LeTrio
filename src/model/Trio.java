package model;

import java.util.Collections;
import java.util.List;

public class Trio {

    private final int id;
    private final int numero;
    private final List<Carte> cartes; // exactement 3 cartes

    public Trio(int id, int numero, List<Carte> cartes) {
        if (cartes == null || cartes.size() != 3) {
            throw new IllegalArgumentException("Un trio doit contenir exactement 3 cartes.");
        }
        this.id = id;
        this.numero = numero;
        this.cartes = List.copyOf(cartes);
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public List<Carte> getCartes() {
        return Collections.unmodifiableList(cartes);
    }
}
