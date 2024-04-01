package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;


//  @OneToOne
//  private Game game;

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

    public void setLobbyUsers(Set<User> lobbyusers) {
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


}
