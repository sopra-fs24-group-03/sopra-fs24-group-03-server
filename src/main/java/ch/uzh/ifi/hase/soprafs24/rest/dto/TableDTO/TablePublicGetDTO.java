package ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO;

import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import java.util.ArrayList;
import java.util.List;

public class TablePublicGetDTO {
    private int money;

    private Long id;

    private List<String> openCardsImage = new ArrayList<>();

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

    public List<String> getOpenCardsImage() {
        return openCardsImage;
    }

    public void setOpenCardsImage(List<Card> openCardsImage) {
        for (Card card : openCardsImage) {
            this.openCardsImage.add(card.getImage());
        }
    }


}
