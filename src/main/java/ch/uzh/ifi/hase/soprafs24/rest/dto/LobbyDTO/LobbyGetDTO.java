package ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;


import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {
    private Long id;

    public Long getId() {
        return id;
    }

    private UserGetDTO lobbyLeader;

    private List<UserGetDTO> lobbyUsers = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    private Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<UserGetDTO> getLobbyUsers() {
        return lobbyUsers;
    }

    public void setLobbyUsers(List<UserGetDTO> lobbyUsers) {
        this.lobbyUsers = lobbyUsers;
    }

    public UserGetDTO getLobbyLeader() {
        return lobbyLeader;
    }

    public void setLobbyLeader(UserGetDTO lobbyLeader) {
        this.lobbyLeader = lobbyLeader;
    }
}