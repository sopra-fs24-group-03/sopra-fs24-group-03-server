package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.externalapi.Card;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

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

    @ManyToOne()
    @JoinColumn(name="game_id", referencedColumnName = "id")
    private Game game;

    public Player(String username, int money, String token, List<Card> cards) {
        setUsername(username);
        setMoney(money);
        setToken(token);
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



    public void fold() {

    }

    public int raise() {
        return 0;
    }

    public int call() {
        return 0;
    }

    public long leaveGame() {
        return 0;
    }

    private int checkMoney() {
        return 0;
    }

}
