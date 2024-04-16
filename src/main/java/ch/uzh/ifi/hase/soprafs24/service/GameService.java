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
    private final UserService userService;

    @Autowired
    public GameService(@Qualifier("userService") UserService userService,@Qualifier("userRepository") UserRepository userRepository, @Qualifier("gameRepository") GameRepository gameRepository){
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }
    public Game getGameById(long id, String token){
        userService.authenticateUser(token);
        Game game = findGame(id);
        return game;

    }


    private Game findGame(long gameId){
        Game game = gameRepository.findById(gameId);
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown game");
        }
        return game;
    }


    //method to make moves
    public int turn(GamePutDTO move, long game_id, String token) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(game_id);

        Player player = game.getPlayerByUsername(username);


        //call the correct method and return the amount bet, if nothing is bet return 0
        return switch (move.getMove()) {
            //TODO fold not done yet
            case Fold -> {
                if(move.getAmount() != 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Fold with an Amount");}
                //remove player from player list, but he "remains" in game
                game.getPlayers().remove(player);
                //no bet was made
                yield 0;
            }
            case Raise -> {
                if (!checkEnoughMoney(player, move.getAmount())){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot bet more money than you have!");
                }
                //if raise is legal, update the order and current bet and call raise
                if(move.getAmount() > game.getBet()){
                    game.setBet(move.getAmount()); //set the current highest bet in game
                    player.setMoney((player.getMoney()- move.getAmount())); //remove money from user
                    game.updateOrder(); //"reset" betting round
                    yield move.getAmount(); //return bet amount
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to bet higher than the current highest bet!");
            }
            case Check -> {
                if(move.getAmount() != 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Check with an Amount");}
                //only check if there was no bet made this betting round
                if (game.getBet() == 0){
                    yield 0;
                }
                else{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only check if there has been no bet made in this betting round");
                }

            }
            case Call -> {
                if (game.getBet() == 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only call if a Bet was made before");}
                if(move.getAmount() != 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Call with an Amount");}
                if (game.getBet() >player.getMoney()){
                    //if player does not have enough money to call, he goes all in and call works
                    player.setMoney(0);
                    yield player.getMoney();
                }
                else{ //if enough money, bet the current bet
                    player.setMoney((player.getMoney()- game.getBet()));
                    yield game.getBet();
                }
            }
        };
    }
    public boolean checkEnoughMoney(Player curPlayer, int amount){
        if (curPlayer.getMoney()>= amount){return true;}
        else {return false;}

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
            //reset betting to 0 after 1 betting round
            game.setBet(0);
            //check if final round to end game
            if(table.getOpenCards().size() == 5){
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
