package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.websocket.ClientEndpoint;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @JsonIgnore //stop recursion
    @OneToOne(mappedBy = "lobby", cascade = CascadeType.ALL)
    private Game game;

    @JsonIgnore //stop recursion
    @OneToMany(mappedBy = "lobby")
    private Set<User> lobbyusers = new HashSet<>();

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

    public Set<User> getLobbyusers() {
        return lobbyusers;
    }

    public void setLobbyusers(Set<User> lobbyUsers) {
        this.lobbyusers = lobbyUsers;
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


    // Used to create a new Game, assign same ID
    public Game createGame(HashMap<String, Integer> players, long id){
        Game game = new Game(players, this, this.id);
        this.game = game;
        return game;
    }

}
