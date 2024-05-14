package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class ThreeOfKind implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        List<Card> hand = new ArrayList<>();

        // Iterate through the list of cards to find three of a kind
        for (int i = 0; i < cards.size() - 2; i++) {
            // Check if the current card and the next two cards have the same value
            if (getValue(cards.get(i)) == getValue(cards.get(i + 1)) && getValue(cards.get(i)) == getValue(cards.get(i + 2))) {
                // If they have the same value, add them to the hand list
                hand.add(cards.get(i));
                hand.add(cards.get(i + 1));
                hand.add(cards.get(i + 2));
                break; // Exit the loop after finding three of a kind
            }
        }

        // If no three of a kind is found, return null
        if (hand.size() != 3) {
            return null;
        }

        // Add the two highest-ranked cards available to the hand
        for (Card card : cards) {
            // Skip the cards that are already in the hand
            if (!hand.contains(card)) {
                hand.add(card);
                // Stop adding cards once the hand size reaches 5
                if (hand.size() == 5) {
                    break;
                }
            }
        }

        // Return the player hand
        PlayerHand result = new PlayerHand();
        result.setHand(Hand.THREE_OF_A_KIND);
        result.setPlayer(player);
        result.setCards(hand);
        return result;
    }
}
