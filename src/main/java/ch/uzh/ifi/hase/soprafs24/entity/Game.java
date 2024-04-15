package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.externalapi.DeckOfCardsApi;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {


    //Required by Springboot, should not be used otherwise
    protected Game() {
        //default constructor
    }


    //Game and Lobby have the same ID, this could be changed, as the lobby is saved within the game already

    public Game(List<User> users) {
        // Selecting a random user to start and sets the playerTurnId
        Random random = new Random();
        this.playerTurnIndex = 0;
        this.bet = 0;

        DeckOfCardsApi cardsApi = new DeckOfCardsApi(new RestTemplate());
        String deckId = cardsApi.postDeck();
        // create the players and give them cards
        setPlayers(users.stream().map(user -> new Player(this, user.getUsername(), user.getMoney(), user.getToken(), cardsApi.drawCards(deckId, 2))).toList());
        // create Table and give five cards
        this.gameTable = new GameTable(cardsApi.drawCards(deckId, 5));

    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private int playerTurnIndex;

    @Column(nullable = false)
    private int bet;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="gameTable_id", referencedColumnName = "id")
    private GameTable gameTable;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(unique=true, nullable = false)
    private Long id;

//    @OneToOne(mappedBy = "game")
//    private Lobby lobby;


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

    public void setsNextPlayerTurnIndex() {
        int numberOfPlayers = players.size();
        this.playerTurnIndex = (this.playerTurnIndex + 1) % numberOfPlayers;
    }

    public long leaveGame() {
        return 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Lobby getLobby() {
//        return lobby;
//    }
//
//    public void setLobby(Lobby lobby) {
//        this.lobby = lobby;
//    }


    public Player getPlayerByUsername(String username){
        for(Player player: players){
            if(Objects.equals(player.getUsername(), username)){
                return player;
            }
        }
        //if player folded
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not part of this game!");
    }

    public void updateOrder(){
        //find player
        List<Player> updatedOrder = new ArrayList<>();

        //reorder list such that given username is first element
        for(int i = playerTurnIndex; i < players.size(); i++){
            updatedOrder.add(players.get(i));
        }
        for (int i = 0; i < playerTurnIndex; i++) {
            updatedOrder.add(players.get(i));
        }
        this.players = updatedOrder;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public GameTable getGameTable() {
          return gameTable;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerTurnIndex() {
        return playerTurnIndex;
    }
}
