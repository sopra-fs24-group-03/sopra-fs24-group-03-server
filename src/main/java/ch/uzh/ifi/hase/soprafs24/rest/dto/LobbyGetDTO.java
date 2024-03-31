package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.HashSet;
import java.util.Set;

public class LobbyGetDTO {
    private Long id;
    public Long getId() {
        return id;
    }

    private String lobbyLeaderUsername;

    private Set<String> lobbyUsernames = new HashSet<>();

    public void setId(Long id) {
        this.id = id;
    }




    public Set<String> getLobbyUsernames() {
        return lobbyUsernames;
    }

    public void setLobbyUsernames(Set<String> lobbyUsernames) {
        this.lobbyUsernames = lobbyUsernames;
    }

    public String getLobbyLeaderUsername() {
        return lobbyLeaderUsername;
    }

    public void setLobbyLeaderUsername(String lobbyLeaderUsername) {
        this.lobbyLeaderUsername = lobbyLeaderUsername;
    }
}
