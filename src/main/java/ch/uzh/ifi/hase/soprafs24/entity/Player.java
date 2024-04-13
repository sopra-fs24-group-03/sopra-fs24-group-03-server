package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Map;

@Entity
public class Player {
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private int money;
    @Column(nullable = false)
    private Map cards;
    @Column(nullable = false, unique = true)
    private String token;

    public Player(String username, int money, String token, Map cards) {
        setUsername(username);
        setMoney(money);
        setToken(token);
        setCards(cards);
    }

    protected Player() {
        //default constructor
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
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public Map getCards() { return cards; }
    public void setCards(Map cards) { this.cards = cards; }


    public void check(){

    }

    public void fold() {

    }

    public int raise(int amount) {
        return 1;
    }

    public int call(int amount) {
        return 1;
    }

    public long leaveGame() {
        return 0;
    }

    private int checkMoney() {
        return 1;
    }

}
