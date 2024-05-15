package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class Straight implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        int length = cards.size();
        List<Card> hand = new ArrayList<>();
        List<Card> specialHand = new ArrayList<>();
        hand.add(cards.get(0));
        int continues = 1;
        for (int i = 0; i < length; i++) {
            if(i<length-1) {
                if (getValue(cards.get(i)) - 1 == getValue(cards.get(i + 1))) {
                    hand.add(cards.get(i + 1));
                    continues += 1;
                }
                else if (getValue(cards.get(i)) != getValue(cards.get(i + 1))) {
                    continues = 1;
                    hand.clear();
                    hand.add(cards.get(i + 1));
                }

                if (continues == 5) {
                    PlayerHand result = new PlayerHand();
                    result.setHand(Hand.STRAIGHT);
                    result.setCards(hand);
                    result.setPlayer(player);
                    return result;
                }
            }
            if (getValue(cards.get(i)) == 14 || getValue(cards.get(i)) <= 5) {
                boolean inside = false;
                for (Card card : specialHand) {
                    if (getValue(card) == getValue(cards.get(i))) {
                        inside = true;
                        break;
                    }
                }
                if (!inside) {
                    specialHand.add(cards.get(i));
                }
            }
        }
        if (specialHand.size() == 5) {
            //put ass at the end
            Card ass = specialHand.remove(0);
            specialHand.add(ass);
            PlayerHand result = new PlayerHand();
            result.setHand(Hand.STRAIGHT);
            result.setCards(specialHand);
            result.setPlayer(player);
            return result;
        }
        return null;
    }
}
