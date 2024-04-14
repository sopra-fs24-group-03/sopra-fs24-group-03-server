package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.websocket.ClientEndpoint;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private final Logger log = LoggerFactory.getLogger(Lobby.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="game_id", referencedColumnName = "id")
    private Game game;

    @JsonIgnore //stop recursion
    @OneToMany(mappedBy = "lobby")
    private List<User> lobbyusers = new ArrayList<>();

    @JsonIgnore //stop recursion
    @OneToOne
    @JoinColumn(name = "lobbyLeader_id")
    private User lobbyLeader;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<User> getLobbyusers() {
        return lobbyusers;
    }

    public void setLobbyusers(List<User> lobbyusers) {
        this.lobbyusers = lobbyusers;
    }

    public void addUserToLobby(User user) {
        lobbyusers.add(user);
    }

    public void removeUserFromLobby(User user) {
        lobbyusers.remove(user);
    }

    public User getLobbyLeader() {
        return lobbyLeader;
    }

    public void setLobbyLeader(User lobbyLeader) {
        this.lobbyLeader = lobbyLeader;
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game){this.game = game;}


    // Used to create a new Game, assign same ID
    public Game createGame(List<User> users){
        Game game = new Game(users);
        this.game = game;
        return game;
    }

}
