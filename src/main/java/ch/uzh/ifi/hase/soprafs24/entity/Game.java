package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    public Game(HashMap<String, Integer> players, Lobby lobby, long id, @Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
        setLobby(lobby);
        setId(id);
        // can be changed for now the first in the list (HashMap) starts
        setPlayer(players);
        // get from Card API for each Player two cards and for Table five

        //TODO constructor for Game
    }

    @JsonIgnore //stop recursion
    @OneToMany(mappedBy = "game")
    List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private long playerTurnId;

    @JsonIgnore
    @OneToOne(mappedBy = "game")
    private Table table;

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    private final UserRepository userRepository;



    public void game() {

    }

    private void setUp() {

    }

    private Player winningCondition() {

    }

    private Map endGame() {

    }

    public Map turn() {

    }

    public long leaveGame() {

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
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            User userToPlayer = UserRepository.findByToken(entry.getKey());
            players.add(new Player(userToPlayer.getUsername(), userToPlayer.getMoney(), userToPlayer.getToken(), //cards))


        }


    }
}
