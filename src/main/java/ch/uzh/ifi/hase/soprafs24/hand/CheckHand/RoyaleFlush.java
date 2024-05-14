package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class RoyaleFlush implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        //The royal flush is the best hand available in poker. It features five consecutive cards, all of the same suit, in order of value from 10 through to ace.
        int length = cards.size();
        List<Card> hand = new ArrayList<>();

        //iterates over every card, in checks if there are five cards of equal suit
        for (int i = 0; i < length; i++) {
            hand.clear();
            char suit = cards.get(i).getCode().charAt(1);
            int number = getValue(cards.get(i));
            if (number == 14) {
                hand.add(cards.get(i));
                for (int j = i + 1; j < length; j++) {
                    if (number - 1 == getValue(cards.get(j)) && suit == cards.get(j).getCode().charAt(1)) {
                        hand.add(cards.get(j));
                        number = getValue(cards.get(j));
                    }
                    else if (number - 1 == getValue(cards.get(j)) || number == getValue((cards.get(j)))) {
                        continue;
                    }
                    else if (number - 1 > getValue(cards.get(j))) {
                        break;
                    }
                    if (hand.size() == 5) {
                        PlayerHand result = new PlayerHand();
                        result.setHand(Hand.ROYAL_FLUSH);
                        result.setCards(hand);
                        result.setPlayer(player);
                        return result;
                    }
                }
            }
        }
        return null;
    }
}
