package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand;
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

import java.util.*;
import java.util.stream.Stream;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;
import static ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand.*;


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
        //TODO only player IN said game/lobby can get game
        return findGame(id);

    }

    public Player getPlayerByToken(List<Player> players, String token) {
        for (Player player: players) {
            if (player.getToken().equals(token)) {
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not in this game");
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
        int amount =  switch (move.getMove()) {
            case Fold -> {
                if(move.getAmount() != 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Fold with an Amount");}
                //set folded attribute to true, but he "remains" in game
                player.setFolded(true);
                //no bet was made
                yield 0;
            }
            case Raise -> {
                if(player.getMoney()<move.getAmount()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot bet more money than you have!");
                }
                //if raise is legal, update the order and current bet and call raise
                if(move.getAmount() > game.getBet()){
                    game.setBet(move.getAmount()); //set the current highest bet in game
                    int moneyLost = move.getAmount()- player.getLastRaiseAmount();
                    player.setMoney((player.getMoney()- moneyLost)); //remove money from user

                    player.setLastRaiseAmount(move.getAmount()); //save the last raised amount for call

                    //set the raise player so that it can be check in update game mehtod
                    game.setRaisePlayer(player);

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
                    //All in call not implemented yet
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only call if you have enough money");
                }
                else{ //if enough money, bet the current bet
                    int loss = game.getBet() - player.getLastRaiseAmount();
                    player.setMoney(player.getMoney()- loss); //p1 raises 100, p2 raises to 200, p1 calls --> only subtract (200-100 = 100) --> in total also 200
                    yield loss;
                }
            }

        };
        game.setsNextPlayerTurnIndex();
        return amount;


    }


    //updates game state (split from turn for easier testing)
    public void updateGame(long game_id, int bet){
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        String nextPlayerUsername = game.getPlayers().get(game.getPlayerTurnIndex()).getUsername();

        if(bet > 0){
            table.updateMoney(bet);
        }

        //check if all players except 1 folded
        if(playersfolded(game)){
            return;

        }

        //check if current betting round is finished
        if((game.getRaisePlayer() != null && Objects.equals(game.getRaisePlayer().getUsername(), nextPlayerUsername))){
            //reset betting to 0 after 1 betting round

            game.setBet(0);
            for (Player player : game.getPlayers()) {
                player.setLastRaiseAmount(0);}

            //check if final round to end game
            if (table.getOpenCards().size() == 5) {
                winningCondition(game_id); //then game ends, call winning condition
                return;
            }
            table.updateOpenCards();
        }


    }


    public void authorize(String token, long id) {
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


    public boolean playersfolded(Game game){
        List<Player> players = game.getPlayers();
        int foldedPlayersCount = 0; // Initialize counter for folded players

        for (Player player : players) {
            if (player.isFolded()) {
                foldedPlayersCount++;
            }
        }
        return (foldedPlayersCount == (players.size() -1));
    }
    //TODO Big blind person canNOT play rn after blinding :(
    public void initializeBlinds(Game game) {
        List<Player> players = game.getPlayers();

        // Set blinds
        int smallBlind = 25;
        int bigBlind = 50;

        // Assume players are in order and rotate as per game rounds
        Player smallBlindPlayer = players.get(0);
        Player bigBlindPlayer = players.get(1);

        GamePutDTO gamePutDTOsmall = new GamePutDTO();
        gamePutDTOsmall.setMove(Moves.Raise);
        gamePutDTOsmall.setAmount(smallBlind);


        authorize(smallBlindPlayer.getToken(), game.getId());
        turn(gamePutDTOsmall, game.getId(),smallBlindPlayer.getToken());
        updateGame(game.getId(), smallBlind);

        GamePutDTO gamePutDTObig = new GamePutDTO();
        gamePutDTObig.setMove(Moves.Raise);
        gamePutDTObig.setAmount(bigBlind);

        authorize(bigBlindPlayer.getToken(), game.getId());
        turn(gamePutDTObig, game.getId(),bigBlindPlayer.getToken());
        updateGame(game.getId(), bigBlind);



    }


    //finds the winning player
    //TODO update the users money + destroy game class
    public void endGame(long game_id){
        System.out.println("END GAME XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }


    public void winningCondition(long game_id) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        PlayerHand winner = null;

        //find the best hand
        for(Player player : game.getPlayers()){

            //ignore folded players
            if(player.isFolded()){
                continue;
            }
            PlayerHand curHand = evaluateHand(player, table);

            curHand.getCards().sort(new Comparator<Card>() {
                @Override
                public int compare(Card c1, Card c2) {
                    return -Integer.compare(getValue(c1), getValue(c2));
                }
            });

            if(winner == null || handRank(curHand.getHand()) > handRank(winner.getHand())){
                winner = curHand;
            }

            //TODO if two player have same hand (check further)
            else if(handRank(curHand.getHand()) == handRank(winner.getHand())){
                if(getValue(curHand.getCards().get(0)) > getValue(winner.getCards().get(0))){
                    winner = curHand;
                }
            }
        }

        //TODO In the end winning condition should call end game so that game is destroyed, players sent back to lobby
        endGame((game_id));
    }


    public PlayerHand evaluateHand(Player player, GameTable table) {

        //combine the player card and the cards on the table
        List<Card> cards = new ArrayList<>(Stream.concat(player.getCards().stream(), table.getCards().stream()).toList());

        //sort the players cards by their Value, from highest to lowest
        cards.sort(new Comparator<Card>() {
            @Override
            public int compare(Card c1, Card c2) {
                return -Integer.compare(getValue(c1), getValue(c2));
            }
        });

        PlayerHand playerHand = null;

        //royal flush
        playerHand = royalFlush(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //straight flush
        playerHand = straightFlush(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //four-of-a-king
        playerHand = fourCards(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //full house
        playerHand = fullHouse(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //flush
        playerHand = flush(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //straight
        playerHand = straight(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //three-of-a-kind
        playerHand = threeOfKind(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //two pair
        playerHand = twoPair(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //pair
        playerHand = pair(cards, player);
        if(playerHand != null){
            return playerHand;
        }

        //high card
        return highCard(cards, player);

    }

}
