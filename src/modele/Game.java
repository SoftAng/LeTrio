package modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private static int counter = 0;
    private final int uniqueId;

    private final List<Player> players;
    private final List<Card> center;
    private final String gameMode; // "SIMPLE" (for now)

    public Game(List<Player> players, String gameMode) {
        this.uniqueId = ++counter;
        this.players = players;
        this.center = new ArrayList<>();
        this.gameMode = gameMode;
    }

    /**
     * Distribute cards according to Trio rules:
     * - 3 players: 9 each, 9 to center
     * - 4 players: 7 each, 8 to center
     * - 5 players: 6 each, 6 to center
     * -     * - 6 players: 5 each, 6 to center
     */
    public void distributeCards(List<Card> cards) {
        Collections.shuffle(cards);

        int n = players.size();
        int cardsPerPlayer = switch (n) {
            case 3 -> 9;
            case 4 -> 7;
            case 5, 6 -> 6; // you can refine later if you prefer 5 per player for 6 players
            default -> throw new IllegalArgumentException("Unsupported number of players: " + n);
        };

        int index = 0;

        // Deal hands
        for (Player p : players) {
            for (int i = 0; i < cardsPerPlayer; i++) {
                p.addCard(cards.get(index++));
            }
            // Sort hand ascending (smallest to largest) to comply with "reveal smallest/largest"
            p.sortHand();
        }

        // Remaining cards go to center (face down)
        while (index < cards.size()) {
            center.add(cards.get(index++));
        }
    }

    public boolean checkVictory(Player p) {
        // Mode SIMPLE: win if player has 3 trios or the special trio (12)
        return p.hasWonSimpleMode();
    }

    public List<Card> getCenter() {
        return center;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return "Game #" + uniqueId + " | Mode: " + gameMode;
    }

