package ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO;


import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;

import java.util.List;

// TODO add necessary Game Attributes
public class GameGetDTO {
    private long id;
    private int money;
    private List<PlayerPublicGetDTO> players;
    private PlayerPrivateGetDTO ownPlayer;
    // we should not send the whole gameTable we have to countrol, that the right amount of cards is sent to the frontend
    private GameTable gameTable;


    // cards of player
    // something that

    // last player which raised
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public List<PlayerPublicGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerPublicGetDTO> players) {
        this.players = players;
    }

    public PlayerPrivateGetDTO getOwnPlayer() {
        return ownPlayer;
    }

    public void setOwnPlayer(PlayerPrivateGetDTO ownPlayer) {
        this.ownPlayer = ownPlayer;
    }
    public void setGameTable(GameTable gameTable) {
        this.gameTable = gameTable;
    }
    public GameTable getGameTable() {
        return gameTable;
    }
}
