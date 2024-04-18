package ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO;

import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

public class TablePublicGetDTO {
    private int money;

    private Long id;

    private List<Card> openCards = new ArrayList<>();

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Card> getOpenCards() {
        return openCards;
    }

    public void setOpenCards(List<Card> openCards) {
        this.openCards = openCards;
    }


}
