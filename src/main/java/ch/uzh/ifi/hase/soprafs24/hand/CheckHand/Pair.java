package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class Pair implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        List<Card> hand = new ArrayList<>();

        // Iterate through the list of cards
        for (int i = 0; i < cards.size() - 1; i++) {
            // Check if the current card and the next card have the same value
            if (getValue(cards.get(i)) == getValue(cards.get(i + 1))) {
                // If they have the same value, add them to the hand list
                hand.add(cards.get(i));
                hand.add(cards.get(i + 1));
                break; // Exit the loop after finding the pair
            }
        }

        // If no pair is found, return null
        if (hand.size() != 2) {
            return null;
        }

        // Add the three highest-ranked cards available to the hand
        for (Card card : cards) {
            // Skip the cards that are already in the hand
            if (!hand.contains(card)) {
                hand.add(card);
            }
            // Stop adding cards once the hand size reaches 5
            if (hand.size() == 5) {
                break;
            }
        }
        // Return the player hand
        PlayerHand result = new PlayerHand();
        result.setHand(Hand.ONE_PAIR);
        result.setPlayer(player);
        result.setCards(hand);
        return result;
    }
}
