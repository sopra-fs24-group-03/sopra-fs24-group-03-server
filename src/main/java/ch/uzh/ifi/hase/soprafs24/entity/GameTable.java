package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAMETABLE")
public class GameTable implements Serializable {

    @Column
    private int totalTableBettingInCurrentRound;


    @Column
    private long playerIdOfLastMove;


    @Column
    private Moves lastMove;

    @Column
    private int lastMoveAmount;



    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "gameTable", cascade = CascadeType.ALL)
    private List<Card> cards;
    @OneToMany(mappedBy = "gameTable", cascade = CascadeType.ALL)
    private List<Pot> pots = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gameTable_id_open") // Adjust the join column as needed
    private List<Card> openCards = new ArrayList<>();


    public GameTable(List<Card> cards) {
        this.cards = cards;
        cards.forEach(card -> card.setGameTable(this));
        setCards(cards);

        Pot mainPot = new Pot(0, "mainPot");
        mainPot.setGameTable(this);
        pots.add(mainPot);
    }

    //default constructor
    public GameTable() {
    }




    public List<Card> getCards() {
        return cards;
    }

    public List<Card> getOpenCards() {
        return openCards;
    }

    public void updateOpenCards() {
        //only add card up until size 5 --> then size is 5 and in gameservice endgame is called
        if (openCards.isEmpty()) {
            openCards.addAll(cards.subList(0, 3));
        }
        else if (openCards.size() == 3 || openCards.size() == 4) {
            openCards.add(cards.get(openCards.size()));
        }
        //if called with already 5 cards open function just does nothing
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getTotalTableBettingInCurrentRound() {
        return totalTableBettingInCurrentRound;
    }

    public void setTotalTableBettingInCurrentRound(int totalTableBettingInCurrentRound) {
        this.totalTableBettingInCurrentRound = totalTableBettingInCurrentRound;
    }

    public List<Pot> getPots() {
        return pots;
    }

    public void setPots(List<Pot> pots) {
        this.pots = pots;
    }
    public Pot getPotByName(String potName) {
        for (Pot pot : pots) {
            if (pot.getName().equals(potName)) {
                return pot;
            }
        }
        return null; // if no pot found
    }

    public void addPot(Pot pot){
            pots.add(pot);
    }
}
