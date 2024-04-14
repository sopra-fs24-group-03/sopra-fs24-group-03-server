package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.externalapi.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameTable {
    private int Money;


    private List<Card> cards = new ArrayList<>();



    public void updateMoney(int amount){}

    public void updateCards(List<Card> cards){}

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = money;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }



}
