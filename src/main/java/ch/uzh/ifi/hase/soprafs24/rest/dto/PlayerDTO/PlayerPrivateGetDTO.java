package ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;

import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.List;

public class PlayerPrivateGetDTO {
    private Long id;
    private String username;
    private int money;
    private List<Card> cards;
    private boolean folded;

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
    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }
}
