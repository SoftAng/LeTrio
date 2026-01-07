package model;

public class Carte {

    private final int id;
    private final int numero;   // 1 à 12
    private final String nom;   // adaptation UTBM (ex: "Examen AP4B")
    private boolean estVisible;

    public Carte(int id, int numero, String nom) {
        this.id = id;
        this.numero = numero;
        this.nom = nom;
        this.estVisible = false;
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public boolean isVisible() {
        return estVisible;
    }

    public void setVisible(boolean estVisible) {
        this.estVisible = estVisible;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "id=" + id +
                ", numero=" + numero +
                ", nom='" + nom + '\'' +
                ", estVisible=" + estVisible +
                '}';
    }
}
