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
    public int turn(GamePutDTO move, long game_id, String token) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(game_id);

        Player player = game.getPlayerByUsername(username);


        //call the correct method and return the amount bet, if nothing is bet return -1
        return switch (move.getMove()) {
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
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to bet higher than the current highest bet!");
            }
            case Check -> {
                player.check();
                //indicates no bet has been made
                yield -1;
            }
            case Call -> player.call(game.getBet());
        };
    }


    //updates game state (split from turn for easier testing)
    public void updateGame(long game_id, int bet, String token){
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();

        if(bet > 0){
            table.updateMoney(bet);
        }

        //check if round is finished
        if(Objects.equals(game.getPlayers().get(game.getPlayers().size() - 1).getUsername(), username)){
            //check if final round to end game
            if(table.getCards().size() == 5){
                endGame();
            }
            table.updateOpenCards();
        }

        game.setsNextPlayerTurnIndex();
    }


    private void endGame(){
        //TODO
    }

    public void authorize(String token, long id){
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(id);

        //Check if valid inputs
        if(username == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can access this when logged in!");
        }
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown game ID");
        }

        //will throw correct exception if not in player list
        Player player = game.getPlayerByUsername(username);

        if(game.getPlayers().get(game.getPlayerTurnIndex()) != player){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "It is not your turn!");
        }
    }

}
