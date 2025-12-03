package modele;

import java.util.ArrayList;
import java.util.List;

public class Player {
	


    private static int counter = 0;
    private final int uniqueId;
    private final String name;
    private final List<Card> hand;
    private final List<Trio> wonTrios;
    private int score;

    public Player(String nom) {
        this.uniqueId = ++counter;
        this.name = nom;
        this.hand = new ArrayList<>();
        this.wonTrios = new ArrayList<>();
        this.score = 0;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card c) {
    	hand.add(c);
    }

    public Card pickUpTheSmallest() {
        return hand.isEmpty() ? null : hand.get(0);
    }

    public Card pickUpTheBiggest() {
        return hand.isEmpty() ? null : hand.get(hand.size() - 1);
    }

    public void addTrio(Trio t) {
    	wonTrios.add(t);
        score += 1;
    }

    /*public boolean aGagneModeSimple() {
        return wonTrios.size() >= 3 || wonTrios.stream().anyMatch(Trio::estTrioSpecial);
    }*/

    @Override
    public String toString() {
        return "Joueur: " + name + " | Score: " + score;
    }


	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
