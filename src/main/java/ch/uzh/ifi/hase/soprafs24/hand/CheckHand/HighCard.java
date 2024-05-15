package ch.uzh.ifi.hase.soprafs24.hand.CheckHand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.List;

public class HighCard implements CheckHand {
    @Override
    public PlayerHand checkHand(List<Card> cards, Player player) {
        PlayerHand result = new PlayerHand();
        result.setHand(Hand.HIGH_CARD);
        result.setPlayer(player);
        result.setCards(cards.subList(0, 5));
        return result;    }
}
