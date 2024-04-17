package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.externalapi.Card;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.externalapi.Card.getValue;

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


    //Implementations to determine Hands, methods should return a PlayerHand class
    //TODO evaluate the remaining hands (currently implemented: straight, flush, high card)
    public static PlayerHand royalFlush(List<Card> cards, Player player){
        //The royal flush is the best hand available in poker. It features five consecutive cards, all of the same suit, in order of value from 10 through to ace.
        return null;
    }

    public static PlayerHand straightFlush(List<Card> cards, Player player){
        return null;
        //Any five cards of successive values in the same suit that’s not a royal flush is a straight flush. A royal flush or a straight flush with
        //cards of higher ranking are the only hands that can beat a straight flush.
    }

    public static PlayerHand fourCards(List<Card> cards, Player player){
        return null;
        //Four of the same card in the four suits. The five-card hand is completed by the highest card among the others on the table or in your hand.
    }

    public static PlayerHand fullHouse(List<Card> cards, Player player){
        return null;
        //Three of the same value card in three different suits plus a different pair of the same rank card
        // in two different suits in one hand. If more than just one player has a full house the player with the highest value three of a kind will win the hand.
    }

    public static PlayerHand flush(List<Card> cards, Player player){
        //Five cards of the same suit in no particular order. If more than one player has a flush, the hand with the highest valued card will win.

        int length = cards.size();
        List<Card> hand = new ArrayList<Card>();

        //iterates over every card, in checks if there are five cards of equal suit
        for(int i = 0; i <= length-5; i++){
            hand.clear();
            hand.add(cards.get(i));
            char suit = cards.get(i).getCode().charAt(1);
            for(int j = i+1; j <= length; j++){
                if(suit == cards.get(j).getCode().charAt(1)){
                    hand.add(cards.get(j));
                }
                else break;
            }

            //if the amount of cards with equal suit if greater than five return them
            if(hand.size() >= 5){
                PlayerHand result = new PlayerHand();
                result.setHand(Hand.FLUSH);
                result.setCards(cards);
                result.setPlayer(player);
                return result;
            }
        }
        return null;
    }

    public static PlayerHand straight(List<Card> cards, Player player) {
        //Five cards of consecutive numerical value composed of more than one suit. An ace can normally
        // rank as low (below a 2) or high (above a king) but not at the same time in one hand.
        int length = cards.size();
        List<Card> hand = new ArrayList<Card>();

        // Check for regular straights
        for (int i = 0; i <= length - 5; i++) {
            boolean isStraight = true;
            hand.clear();
            for (int j = i; j < i + 4; j++) {
                hand.add(cards.get(j));
                if (getValue(cards.get(j)) != getValue(cards.get(j + 1)) + 1) {
                    isStraight = false;
                    break;
                }
            }
            if (isStraight) {
                PlayerHand result = new PlayerHand();
                result.setHand(Hand.STRAIGHT);
                result.setCards(cards);
                result.setPlayer(player);
                return result;
            }
        }

        // Special case for Ace-low straight (A-2-3-4-5)
        if (getValue(cards.get(length - 1)) == 14 && getValue(cards.get(0)) == 2) {
            hand.clear();
            hand.add(cards.get(length-1));
            // Check if the sequence A-2-3-4-5 exists
            for (int i = 0; i < 4; i++) {
                if (getValue(cards.get(length - 1 - i)) != getValue(cards.get(length - i - 2)) -1) {
                    hand.add(cards.get(length - 1 - i));
                }
                else break;
            }
            if(hand.size() == 5){
                PlayerHand result = new PlayerHand();
                result.setHand(Hand.STRAIGHT);
                result.setCards(cards);
                result.setPlayer(player);
                return result;
            }
        }

        //Player does not have a straight
        return null;
    }

    public static PlayerHand threeOfKind(List<Card> cards, Player player){
        //A poker hand containing three cards of the same rank in three different suits. The two highest available cards besides the three of a kind complete the hand.
        return null;
    }

    public static PlayerHand twoPair(List<Card> cards, Player player){
        //Two different sets of two cards of matching rank. The highest-ranked left available card completes the hand.
        return null;
    }

    public static PlayerHand pair(List<Card> cards, Player player){
        //A pair of cards of the same rank in different suits. The remainder of the hand is formed from the three highest-ranked cards available.
        return null;
    }





    public static PlayerHand highCard(List<Card> cards, Player player) {
        List<Card> hand = new ArrayList<Card>();
        hand.add(cards.get(cards.size()-1));
        PlayerHand result = new PlayerHand();
        result.setHand(Hand.HIGH_CARD);
        result.setPlayer(player);
        result.setCards(hand);
        return result;
    }



    //returns the hands value as a int
    public static int handRank(Hand hand){
        //The lowest-ranked hand available. The highest card in the hand is your ‘best hand’. In this case it's the king of clubs.
        return switch (hand){
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
