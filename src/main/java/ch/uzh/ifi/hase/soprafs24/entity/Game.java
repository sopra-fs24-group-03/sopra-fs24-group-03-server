package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "GAME")
@Service
@Transactional
public class Game {


    //Required by Springboot, should not be used otherwise
    protected Game() {
        //default constructor
    }


    //Game and Lobby have the same ID, this could be changed, as the lobby is saved within the game already
    //@Autowired
    public Game(HashMap<String, Integer> players, Lobby lobby, long id) {
        //this.userRepository = userRepository;
        setLobby(lobby);
        setId(id);
        // can be changed for now the first in the list (HashMap) starts
        //setPlayer(players);
        // get from Card API for each Player two cards and for Table five

        //TODO constructor for Game
    }

    @JsonIgnore //stop recursion
    @OneToMany(mappedBy = "game")
    List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private long playerTurnId;

    @Transient
    private GameTable gameTable;

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;


    //TODO instantiate correctly
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "order", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "order", nullable = false)
    private List<String> order = new ArrayList<>();

    @Column(nullable = false)
    private String playerTurn;

    @Column(nullable = false)
    private int bet  = 0;


    public void game() {

    }

    private void setUp() {

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

    public void setPlayers(HashMap<String, Integer> players) {
//        for (Map.Entry<String, Integer> entry : players.entrySet()) {
//            User userToPlayer = UserRepository.findByToken(entry.getKey());
//            players.add(new Player(userToPlayer.getUsername(), userToPlayer.getMoney(), userToPlayer.getToken(), //cards))
//
//
//        }
    }

    public Player getPlayer(String username){
        //TODO
        return new Player();
    }

    public GameTable getTable(){
        //TODO
        return new GameTable();
    }


    public List<String> getOrder() {
        return order;
    }

    //called if player raises, reset the order where player is now first
    public void updateOrder(){
        int index = order.indexOf(this.playerTurn);
        List<String> updatedOrder = new ArrayList<>();

        //reorder list such that given username is first element
        for(int i = index; i < order.size(); i++){
            updatedOrder.add(order.get(i));
        }
        for (int i = 0; i < index; i++) {
            updatedOrder.add(order.get(i));
        }
        this.order = updatedOrder;
    }


    public void updatePlayerTurn(){
        if(Objects.equals(order.get(order.size() - 1), playerTurn)) {
            playerTurn = order.get(0);
        } else playerTurn = order.get(order.indexOf(playerTurn) + 1);
    }

    public String getPlayerTurn() {
        return playerTurn;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
