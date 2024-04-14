package ch.uzh.ifi.hase.soprafs24.externalapi;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id",unique=true, nullable = false)
    private Long id;

    public void setPlayer(Player player) {
        this.player = player;
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="player_id", referencedColumnName = "id")
    private Player player;

    protected Card() {}
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
}
