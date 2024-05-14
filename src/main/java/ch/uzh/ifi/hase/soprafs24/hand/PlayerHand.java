package ch.uzh.ifi.hase.soprafs24.hand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.List;

public class PlayerHand {
    private Player player;
    private Hand hand;
    private List<Card> cards;

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    //returns the hands value as a int
    public static int handRank (Hand hand){
        //The lowest-ranked hand available. The highest card in the hand is your ‘best hand’.
        return switch (hand) {
            case HIGH_CARD -> 1;
            case ONE_PAIR -> 2;
            case TWO_PAIR -> 3;
            case THREE_OF_A_KIND -> 4;
            case STRAIGHT -> 5;
            case FLUSH -> 6;
            case FULL_HOUSE -> 7;
            case FOUR_OF_A_KIND -> 8;
            case STRAIGHT_FLUSH -> 9;
            case ROYAL_FLUSH -> 10;
            };
        }
    }
