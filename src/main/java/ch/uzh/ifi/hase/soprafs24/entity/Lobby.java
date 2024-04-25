package ch.uzh.ifi.hase.soprafs24.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
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

    public void setGame(Game game) {
        this.game = game;
    }

    public void setGameToNull() {
        this.game = null;
    }

    // Used to create a new Game, assign same ID
    public Game createGame(List<User> users) {
        Game game = new Game(users);
        this.game = game;
        return game;
    }

}
