package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.externalapi.Card;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gameTable_id")
    private List<Card> cards = new ArrayList<>();




    public GameTable(List<Card> cards){
        this.cards = cards;
        Money = 0;
        //no cards yet added to opencards since pre-flop bets
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
        //MODIFY ONLY DISPLAY CARDS AS NEEDED
        return cards;
    }

    //TODO
    public void updateCards(){

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
