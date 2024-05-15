package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class TwoPair implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        List<Card> hand = new ArrayList<>();

        // Iterate through the list of cards to find two pairs
        for (int i = 0; i < cards.size() - 1; i++) {
            if (getValue(cards.get(i)) == getValue(cards.get(i + 1)) && !hand.contains(cards.get(i))) {
                // Check if we already have one pair and the current pair is different
                if (hand.size() == 2 && getValue(hand.get(0)) != getValue(cards.get(i)) && getValue(hand.get(1)) != getValue(cards.get(i))) {
                    hand.add(cards.get(i));
                    hand.add(cards.get(i + 1));
                    break; // Exit the loop after finding the second pair
                }
                else if (hand.isEmpty()) {
                    hand.add(cards.get(i));
                    hand.add(cards.get(i + 1));
                }
            }
        }

        // If not two pairs found, return null
        if (hand.size() != 4) {
            return null;
        }

        // Add the highest-ranked card available to the hand that is not part of the pairs
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
        result.setHand(Hand.TWO_PAIR);
        result.setPlayer(player);
        result.setCards(hand);
        return result;
    }
}
