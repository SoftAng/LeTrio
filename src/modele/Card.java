package modele;

public class Card {
	
	private static int counter = 0;
	private final int uniqueId;
	private final int number;
	private final String name;
	
	public Card(int number, String name) {
		this.uniqueId = ++counter;
		this.number = number;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}


	public int getUniqueId() {
		return uniqueId;
	}


	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + "(numero: " + number +")";
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
