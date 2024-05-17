package ch.uzh.ifi.hase.soprafs24.moves;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs24.constant.Moves.Call;

public class Call implements Actions {
    @Override
    public int makeMove(Player player, GamePutDTO move, Game game) {
        GameTable gameTable = game.getGameTable();

        if (game.getBet() - player.getLastRaiseAmount() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only call if a Bet was made before");
        }
        if (move.getAmount() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Call with an Amount");
        }
        if (game.getBet() >= player.getMoney() + player.getLastRaiseAmount()) { //ALL IN
            player.setAllIn(true);
            int playerMoney = player.getMoney();
            player.setMoney(0);
            player.setLastRaiseAmount(playerMoney);
            setMoveInGameTable(gameTable, Call, 0, player.getId());

            gameTable.setTotalTableBettingInCurrentRound(gameTable.getTotalTableBettingInCurrentRound() + playerMoney);
            player.setTotalBettingInCurrentRound(player.getTotalBettingInCurrentRound() + playerMoney);

            player.setTotalBettingOverall(player.getTotalBettingOverall() + playerMoney);

            if (game.getRaisePlayer() == player) {
                game.setRaisePlayer(null);
            }
            return playerMoney;


        }
        else { //if enough money, bet the current bet
            int loss = game.getBet() - player.getLastRaiseAmount();
            player.setMoney(player.getMoney() - loss); //p1 raises 100, p2 raises to 200, p1 calls --> only subtract (200-100 = 100) --> in total also 200
            player.setLastRaiseAmount(player.getLastRaiseAmount() + loss);
            setMoveInGameTable(gameTable, Call, 0, player.getId());

            //set the amounts of current betting round
            gameTable.setTotalTableBettingInCurrentRound(gameTable.getTotalTableBettingInCurrentRound() + loss);
            player.setTotalBettingInCurrentRound(player.getTotalBettingInCurrentRound() + loss);
            player.setTotalBettingOverall(player.getTotalBettingOverall() + loss);

            //return amount to be updated in table
            return loss;
        }
    }
}
