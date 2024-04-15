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
    public int turn(GamePutDTO move, long id, String token) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(id);

        Player player = game.getPlayer(username);


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
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
    public void updateGame(long id, int bet, String token){
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(id);
        GameTable table = game.getTable();

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

        //Check if legal actions
        if(!game.getOrder().contains(username)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not in this game!");
        }else if(!Objects.equals(game.getPlayerTurn(), username)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "It is not your turn!");
        }
    }

}
