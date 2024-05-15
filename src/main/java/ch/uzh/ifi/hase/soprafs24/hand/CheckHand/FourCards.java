package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class FourCards implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        //Four of the same card in the four suits. The five-card hand is completed by the highest card among the others on the table or in your hand.
        List<Card> hand = new ArrayList<>();

        // Iterate through the list of cards to find four of a kind
        for (int i = 0; i < cards.size() - 3; i++) {
            // Check if the current card and the next three cards have the same value
            if (getValue(cards.get(i)) == getValue(cards.get(i + 1)) &&
                    getValue(cards.get(i)) == getValue(cards.get(i + 2)) &&
                    getValue(cards.get(i)) == getValue(cards.get(i + 3))) {
                // If they have the same value, add them to the hand list
                hand.add(cards.get(i));
                hand.add(cards.get(i + 1));
                hand.add(cards.get(i + 2));
                hand.add(cards.get(i + 3));
                break; // Exit the loop after finding four of a kind
            }
        }

        // If no four of a kind is found, return null
        if (hand.size() != 4) {
            return null;
        }

        // Add the highest-ranked card available to the hand that is not part of the four of a kind
        for (Card card : cards) {
            if (!hand.contains(card)) {
                hand.add(card);
                break; // Only one additional card is needed to complete the hand
            }
        }

        // Return the player hand
        PlayerHand result = new PlayerHand();
        result.setHand(Hand.FOUR_OF_A_KIND);
        result.setPlayer(player);
        result.setCards(hand);
        return result;
    }
}
