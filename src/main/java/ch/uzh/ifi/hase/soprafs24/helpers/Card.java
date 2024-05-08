package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "gameTable_id", referencedColumnName = "id")
    private GameTable gameTable;

    public void setGameTable(GameTable gameTable) {
        this.gameTable = gameTable;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    protected Card() {
    }

    public String getCode() {
        return code;
    }

    @Column(nullable = false)
    private String code;

    public String getImage() {
        return image;
    }

    @Column(nullable = false)
    private String image;

    public Card(String code, String image) {
        this.code = code;
        this.image = image;
    }

    //returns a numeric value of the card
    public static int getValue(Card card) {
        char cardValue = card.getCode().charAt(0); //get the cards value
        return switch (cardValue) {
            case '2' -> 2;
            case '3' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '7' -> 7;
            case '8' -> 8;
            case '9' -> 9;
            case '0' -> 10; //0 represents ten in the card codes
            case 'J' -> 11;
            case 'Q' -> 12;
            case 'K' -> 13;
            case 'A' -> 14;
            default -> -1; // Invalid card, no error should be raised just an indication it is invalid
        };
    }
}
