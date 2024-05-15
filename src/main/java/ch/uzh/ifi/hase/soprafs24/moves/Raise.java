package ch.uzh.ifi.hase.soprafs24.moves;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static ch.uzh.ifi.hase.soprafs24.constant.Moves.Raise;

public class Raise implements Actions {
    @Override
    public int makeMove(Player player, GamePutDTO move, Game game) {
        GameTable gameTable = game.getGameTable();

        if (player.getMoney() < move.getAmount() - player.getLastRaiseAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot bet more money than you have!");
        }
        //if raise is legal, update the order and current bet and call raise
        if (move.getAmount() > game.getBet()) {
            game.setBet(move.getAmount()); //set the current highest bet in game
            int moneyLost = move.getAmount() - player.getLastRaiseAmount();
            player.setMoney((player.getMoney() - moneyLost)); //remove money from user

            player.setLastRaiseAmount(move.getAmount()); //save the last raised amount for call

            //set the raise player so that it can be check in update game method
            game.setRaisePlayer(player);
            setMoveInGameTable(gameTable,Raise,move.getAmount(),player.getId());

            //set the amounts of current betting round
            gameTable.setTotalTableBettingInCurrentRound(gameTable.getTotalTableBettingInCurrentRound() + moneyLost);
            player.setTotalBettingInCurrentRound(player.getTotalBettingInCurrentRound()+moneyLost);

            //if he went all in
            if(player.getMoney() == 0){
                player.setAllIn(true);
                if (game.getRaisePlayer() == player) {
                    game.setRaisePlayer(null);
                }
            }

            return moneyLost; //return bet amount
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to bet higher than the current highest bet!");
    }
}
