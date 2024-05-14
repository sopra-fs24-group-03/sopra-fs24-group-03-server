package ch.uzh.ifi.hase.soprafs24.moves;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs24.constant.Moves.Fold;

public class Fold implements Actions {
    @Override
    public int makeMove(Player player, GamePutDTO move, Game game) {
        GameTable gameTable = game.getGameTable();

        if (move.getAmount() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Fold with an Amount");
        }
        //set folded attribute to true, but he "remains" in game
        setMoveInGameTable(gameTable,Fold,0,player.getId());
        player.setFolded(true);
        game.setsNextPlayerTurnIndex();
        if (game.getRaisePlayer() == player) {
            game.setRaisePlayer(null);
        }
        //no bet was made
        return  0;
    }
}
