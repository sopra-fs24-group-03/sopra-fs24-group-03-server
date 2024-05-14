package ch.uzh.ifi.hase.soprafs24.moves;

import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;

public interface Actions {
    int makeMove(Player player, GamePutDTO move, Game game);

    default void setMoveInGameTable(GameTable gameTable, Moves move, int amount, long playerId){
        if(gameTable != null) {
            gameTable.setLastMove(move);
            gameTable.setLastMoveAmount(amount);
            gameTable.setPlayerIdOfLastMove(playerId);
        }
    }
}
