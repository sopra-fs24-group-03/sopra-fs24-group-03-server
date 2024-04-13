package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.externalapi.DeckOfCardsApi;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "game")
public class Game {


    //Required by Springboot, should not be used otherwise
    protected Game() {
        //default constructor
    }


    //Game and Lobby have the same ID, this could be changed, as the lobby is saved within the game already

    public Game(List<User> users) {
        DeckOfCardsApi cardsApi = new DeckOfCardsApi(new RestTemplate());
        String deckId = cardsApi.postDeck();
        // create the players and give them cards
        setPlayers(users.stream().map(user -> new Player(user.getUsername(), user.getMoney(), user.getToken(), cardsApi.drawCards(deckId, 2))).toList());
        // TODO create a table and assign cards
    }

    @JsonIgnore //stop recursion
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private long playerTurnId;

    @Transient
    private GameTable gameTable;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(unique=true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;


    public void game() {

    }

    private void setUp() {

    }

//    private Player winningCondition() {
//
//    }
//
//    private Map endGame() {
//
//    }
//
//    public Map turn() {
//
//    }

    public long leaveGame() {
        return 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
