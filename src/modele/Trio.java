package modele;

import java.util.List;

public class Trio {
	
	private static int counter = 0;
	private final int uniqueId;
	private final int number;
	private final List<Card> trioCards;
	
	
	public Trio(int number, List<Card> cards) {
		
		this.uniqueId = ++counter;
		this.number = number;
		this.trioCards = cards;
	}
	
	public boolean estComplet() {
		
		return (trioCards.size() == 3);
	}
	
	public boolean estTrioSpecial() {
		
		return number == 12;
	}
	
	
	@Override
	public String toString() {
		
		return "Trio de: "+ number +" ("+ trioCards.size() + " cartes";
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
