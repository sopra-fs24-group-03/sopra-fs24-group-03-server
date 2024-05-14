package ch.uzh.ifi.hase.soprafs24.moves;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs24.constant.Moves.Check;

public class Check implements Actions {
    @Override
    public int makeMove(Player player, GamePutDTO move, Game game) {
        GameTable gameTable = game.getGameTable();

        if (move.getAmount() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Check with an Amount");
        }
        //only check if there was no bet made this betting round
        if (game.getBet()-player.getLastRaiseAmount() == 0) {
            setMoveInGameTable(gameTable,Check,0,player.getId());
            return 0;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only check if there has been no bet made in this betting round");
        }
    }
}
