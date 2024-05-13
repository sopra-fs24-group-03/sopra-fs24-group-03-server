package ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;

import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

public class PlayerPrivateGetDTO {
    private Long id;
    private String username;
    private int money;
    private int profit;
    private List<String> cardsImage = new ArrayList<>();
    private boolean folded;

    private boolean turn;

    private boolean AllIn;

    private int lastRaiseAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public List<String> getCardsImage() {
        return cardsImage;
    }

    public void setCardsImage(List<Card> cards) {
        for (Card card : cards) {
            this.cardsImage.add(card.getImage());
        }
    }

    public void deleteCardsImage(){
        this.cardsImage.clear();
    }
    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public boolean getFolded() {
        return folded;
    }

    public boolean isAllIn() {
        return AllIn;
    }

    public void setAllIn(boolean allIn) {
        AllIn = allIn;
    }
    public int getLastRaiseAmount() {
        return lastRaiseAmount;
    }

    public void setLastRaiseAmount(int lastRaiseAmount) {
        this.lastRaiseAmount = lastRaiseAmount;
    }


}
