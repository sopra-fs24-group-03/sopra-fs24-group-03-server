package ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;

public class PlayerPublicGetDTO {


    private long id;
    private int money;
    private String username;
    private boolean folded;
    private boolean turn;

    private boolean AllIn;
    private int lastRaiseAmount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

