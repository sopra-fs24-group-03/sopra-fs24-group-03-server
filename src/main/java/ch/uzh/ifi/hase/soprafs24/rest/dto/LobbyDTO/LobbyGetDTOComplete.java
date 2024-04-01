package ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.HashSet;
import java.util.Set;

public class LobbyGetDTOComplete {
    private Long id;
    private Set<User> lobbyusers = new HashSet<>();
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
