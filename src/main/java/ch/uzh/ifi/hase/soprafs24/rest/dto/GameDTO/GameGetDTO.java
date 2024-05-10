package ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO;


import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PotDTO.PotPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO.TablePublicGetDTO;

import java.util.ArrayList;
import java.util.List;

public class GameGetDTO {
    private long id;
    // pot ???
    private int currentBet;
    private List<PlayerPublicGetDTO> players;
    private PlayerPrivateGetDTO ownPlayer;
    // we should not send the whole gameTable we have to control, that the right amount of cards is sent to the frontend
    private TablePublicGetDTO gameTable;


    private boolean gameFinished;

    private List<PlayerPrivateGetDTO> winner;
    private List<PlayerPrivateGetDTO> notFoldedPlayers;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
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

    public void setGameTable(TablePublicGetDTO gameTable) {
        this.gameTable = gameTable;
    }

    public TablePublicGetDTO getGameTable() {
        return gameTable;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public List<PlayerPrivateGetDTO> getWinner() {
        return winner;
    }

    public void addWinner(PlayerPrivateGetDTO winner) {
        if(this.winner == null){
            this.winner = new ArrayList<>();
        }
        this.winner.add(winner);
    }

    public void noWinner() {
        this.winner = null;
    }

    public List<PlayerPrivateGetDTO> getNotFoldedPlayers() {
        return notFoldedPlayers;
    }

    public void setNotFoldedPlayers(List<PlayerPrivateGetDTO> notFoldedPlayers) {
        this.notFoldedPlayers = notFoldedPlayers;
    }
}
