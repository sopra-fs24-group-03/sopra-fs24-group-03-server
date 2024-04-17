package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.externalapi.Card;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAMETABLE")
public class GameTable implements Serializable {
    @Column
    private int Money;


    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "gameTable", cascade = CascadeType.ALL)
    private List<Card> cards;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gameTable_id_open") // Adjust the join column as needed
    private List<Card> openCards = new ArrayList<>();



    public GameTable(List<Card> cards){
        this.cards = cards;
        cards.forEach(card -> card.setGameTable(this));
        setCards(cards);
        Money = 0;
    }

    //default constructor
    public GameTable() {
    }


    public void updateMoney(int amount){
        Money += amount;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = money;
    }

    public List<Card> getCards() {
        return cards;
    }
    public List<Card> getOpenCards() {
        return openCards;
    }

    public void updateOpenCards(){
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


}
