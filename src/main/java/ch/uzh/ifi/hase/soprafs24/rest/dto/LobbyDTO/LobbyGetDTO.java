package ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;



import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;

import java.util.HashSet;
import java.util.Set;

public class LobbyGetDTO {
    private Long id;
    public Long getId() {
        return id;
    }

    private UserGetDTO lobbyLeaderUsername;

    private Set<UserGetDTO> lobbyUsernames = new HashSet<>();

    public void setId(Long id) {
        this.id = id;
    }




    public Set<UserGetDTO> getLobbyUsernames() {
        return lobbyUsernames;
    }

    public void setLobbyUsernames(Set<UserGetDTO> lobbyUsernames) {
        this.lobbyUsernames = lobbyUsernames;
    }

    public UserGetDTO getLobbyLeaderUsername() {
        return lobbyLeaderUsername;
    }

    public void setLobbyLeaderUsername(UserGetDTO lobbyLeaderUsername) {
        this.lobbyLeaderUsername = lobbyLeaderUsername;
    }
}