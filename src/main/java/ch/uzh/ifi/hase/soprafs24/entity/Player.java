package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.externalapi.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id",unique=true, nullable = false)
    private Long id;
    protected Player(){}
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private int money;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Card> cards;
    @Column(nullable = false, unique = true)
    private String token;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name="game_id", referencedColumnName = "id")
    private Game game;

    public Player(Game game, String username, int money, String token, List<Card> cards) {
        this.game = game;
        setUsername(username);
        setMoney(money);
        setToken(token);
        System.out.println("Setting cards for player id " + id);
        cards.forEach(card -> card.setPlayer(this));
        setCards(cards);
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
    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }


    public long leaveGame() {
        return 0;
    }

    private int checkMoney() {
        return 0;
    }

}
