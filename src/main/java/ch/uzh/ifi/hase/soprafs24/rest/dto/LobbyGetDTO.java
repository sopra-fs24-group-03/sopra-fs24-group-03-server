package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.HashSet;
import java.util.Set;

public class LobbyGetDTO {
    private Long id;
    public Long getId() {
        return id;
    }
    private User lobbyLeader;
    private Set<User> lobbyusers = new HashSet<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getLobbyusers() {
        return lobbyusers;
    }

    public void setLobbyusers(Set<User> lobbyusers) {
        this.lobbyusers = lobbyusers;
    }

    public User getLobbyLeader() {
        return lobbyLeader;
    }

    public void setLobbyLeader(User lobbyLeader) {
        this.lobbyLeader = lobbyLeader;
    }
}
