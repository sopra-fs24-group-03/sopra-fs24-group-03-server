package ch.uzh.ifi.hase.soprafs24.entity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameTable {
    private int Money;
    List<HashMap<String, String>> cardList = new ArrayList<>();



    public void updateMoney(int amount){}
    public void updateCards(){}

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = money;
    }

    public List<HashMap<String, String>> getCardList() {
        return cardList;
    }
}
