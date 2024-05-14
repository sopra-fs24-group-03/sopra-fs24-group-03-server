package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class StraightFlush implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        int length = cards.size();
        List<Card> hand = new ArrayList<>();

        //iterates over every card, in checks if there are five cards of equal suit
        for (int i = 0; i < length; i++) {
            hand.clear();
            char suit = cards.get(i).getCode().charAt(1);
            int number = getValue(cards.get(i));
            hand.add(cards.get(i));
            for (int j = i+1 ; j < length; j++) {
                if (number-1 == getValue(cards.get(j)) && suit == cards.get(j).getCode().charAt(1)) {
                    hand.add(cards.get(j));
                    number = getValue(cards.get(j));
                }
                else if(number-1 == getValue(cards.get(j)) || number == getValue((cards.get(j)))){
                    continue;
                }
                else if(number-1>getValue(cards.get(j))){
                    break;
                }
                if (hand.size() == 5) {
                    PlayerHand result = new PlayerHand();
                    result.setHand(Hand.STRAIGHT_FLUSH);
                    result.setCards(hand);
                    result.setPlayer(player);
                    return result;
                }
            }
        }
        List<Card> specialHand = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (getValue(cards.get(i)) == 14) {
                Card ass = cards.get(i);
                for (int j = i + 1; j < length; j++) {
                    if (getValue(cards.get(j)) <= 5 && cards.get(j).getCode().charAt(1) == ass.getCode().charAt(1)) {
                        specialHand.add(cards.get(j));
                    }
                }
                specialHand.add(ass);
                if(specialHand.size() == 5){
                    PlayerHand result = new PlayerHand();
                    result.setHand(Hand.STRAIGHT_FLUSH);
                    result.setCards(specialHand);
                    result.setPlayer(player);
                    return result;
                }
            }
        }
        return null;
    }
}
