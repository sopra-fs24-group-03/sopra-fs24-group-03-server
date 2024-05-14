package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;

public class FullHouse implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        int length = cards.size();
        List<Card> threeOfKind = new ArrayList<>();
        List<Card> pair = new ArrayList<>();

        boolean foundPair = false;
        boolean foundThree = false;

        for (int i = 0; i <= length - 5; i++) {
            //check if the next 3 cards are the same and a three of kind has not yet been found
            if (getValue(cards.get(i)) == getValue(cards.get(i + 1)) && getValue(cards.get(i)) == getValue(cards.get(i + 2)) && !foundThree) {
                foundThree = true;
                threeOfKind.add(cards.get(i));
                threeOfKind.add(cards.get(i + 1));
                threeOfKind.add(cards.get(i + 2));
            }
            //otherwise check for a pair
            else if (getValue(cards.get(i)) == getValue(cards.get(i + 1))) {
                foundPair = true;
                pair.add(cards.get(i));
                pair.add(cards.get(i + 1));
            }

            //return the results if a pair and three of kind have been found
            if (foundPair && foundThree) {
                PlayerHand result = new PlayerHand();
                result.setPlayer(player);

                //concat the the found pair and three of kind, insure the TOK comes first
                result.setCards(Stream.concat(threeOfKind.stream(), pair.stream()).toList());
                result.setHand(Hand.FULL_HOUSE);
                return result;
            }
        }

        //return a null if nothing has been found
        return null;
    }
}
