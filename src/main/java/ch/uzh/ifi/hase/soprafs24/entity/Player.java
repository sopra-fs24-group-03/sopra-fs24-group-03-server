package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    protected Player() {
    }

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private int money;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Card> cards;
    @Column(nullable = false, unique = true)
    private String token;
    @Column
    private int lastRaiseAmount;
    @Column
    private boolean folded;
    @Column
    private boolean AllIn;
    @Column
    private int totalBettingInCurrentRound;
    @Column
    private int profit;



    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;
    @JsonIgnore
    @ManyToMany(mappedBy = "eligiblePlayers")
    private List<Pot> pots = new ArrayList<>();


    public Player(Game game, String username, int money, String token, List<Card> cards) {
        this.game = game;
        this.folded = false;
        setUsername(username);
        setMoney(money);
        setToken(token);
        System.out.println("Setting cards for player id " + id);
        cards.forEach(card -> card.setPlayer(this));
        setCards(cards);
        lastRaiseAmount = 0;
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

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }


    public long leaveGame() {
        return 0;
    }

    private int checkMoney() {
        return 0;
    }

    public int getLastRaiseAmount() {
        return lastRaiseAmount;
    }

    public void setLastRaiseAmount(int lastRaiseAmount) {
        this.lastRaiseAmount = lastRaiseAmount;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAllIn() {
        return AllIn;
    }

    public void setAllIn(boolean allIn) {
        AllIn = allIn;
    }

    public int getTotalBettingInCurrentRound() {
        return totalBettingInCurrentRound;
    }

    public void setTotalBettingInCurrentRound(int totalBettingInCurrentRound) {
        this.totalBettingInCurrentRound = totalBettingInCurrentRound;
    }

    public List<Pot> getPots() {
        return pots;
    }

    public void setPots(List<Pot> pots) {
        this.pots = pots;
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
}
