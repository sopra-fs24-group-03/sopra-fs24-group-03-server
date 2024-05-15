package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

public class Flush implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        int length = cards.size();
        List<Card> hand = new ArrayList<>();

        //iterates over every card, in checks if there are five cards of equal suit
        for (int i = 0; i < length; i++) {
            hand.clear();
            char suit = cards.get(i).getCode().charAt(1);
            hand.add(cards.get(i));
            for (int j = i + 1; j < length; j++) {
                if (suit == cards.get(j).getCode().charAt(1)) {
                    hand.add(cards.get(j));
                }
                if (hand.size() == 5) {
                    PlayerHand result = new PlayerHand();
                    result.setHand(Hand.FLUSH);
                    result.setCards(hand);
                    result.setPlayer(player);
                    return result;
                }
            }
        }
        return null;
    }
}
