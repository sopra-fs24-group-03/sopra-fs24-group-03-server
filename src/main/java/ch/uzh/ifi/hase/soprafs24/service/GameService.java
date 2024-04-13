package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Autowired
    public GameService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("gameRepository") GameRepository gameRepository){
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }


    //method to make moves
    public void turn(GamePutDTO move, long id, String token) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(id);
        authorize(username, game);

        GameTable table = game.getTable();
        Player player = game.getPlayer(username);


        //call the correct method and return the amount bet, if nothing is bet return -1
        int bet = switch (move.getMove()) {
            case Fold -> {
                player.fold();
                //indicates no bet has been made
                yield -1;
            }
            case Raise -> {
                //if raise is legal, update the order and current bet and call raise
                if(move.getAmount() > game.getBet()){
                    game.updateOrder();
                    game.setBet(move.getAmount());
                    yield player.raise(move.getAmount());
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            case Check -> {
                player.check();
                //indicates no bet has been made
                yield -1;
            }
            case Call -> player.call(game.getBet());
        };

        if(bet > 0){
            table.updateMoney(bet);
        }

        //check if round is finished
        if(Objects.equals(game.getOrder().get(game.getOrder().size() - 1), username)){
            //check if final round to end game
            if(table.getCardList().size() == 3){
                endGame();
            }
            table.updateCards();
        }

        game.updatePlayerTurn();
    }


    private void endGame(){
        //TODO
    }

    private void authorize(String username, Game game){
        if(!game.getOrder().contains(username)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not in this game!");
        }else if(!Objects.equals(game.getPlayerTurn(), username)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "it is not your turn!");
        }
    }

}
