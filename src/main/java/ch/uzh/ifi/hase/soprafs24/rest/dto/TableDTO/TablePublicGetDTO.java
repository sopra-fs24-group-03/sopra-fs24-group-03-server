package ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO;

import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

public class TablePublicGetDTO {
    private int money;

    private Long id;

    private List<String> openCardsImage = new ArrayList<>();

    private long playerIdOfLastMove;


    private Moves lastMove;

    private int lastMoveAmount;

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
    public int getLastMoveAmount() {
        return lastMoveAmount;
    }

    public void setLastMoveAmount(int lastMoveAmount) {
        this.lastMoveAmount = lastMoveAmount;
    }

    public long getPlayerIdOfLastMove() {
        return playerIdOfLastMove;
    }

    public void setPlayerIdOfLastMove(long playerIdOfLastMove) {
        this.playerIdOfLastMove = playerIdOfLastMove;
    }

    public Moves getLastMove() {
        return lastMove;
    }

    public void setLastMove(Moves lastMove) {
        this.lastMove = lastMove;
    }

}
