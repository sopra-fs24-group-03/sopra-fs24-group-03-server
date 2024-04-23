package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameTable;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.helpers.ScheduledGameDelete;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
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
    private final LobbyRepository lobbyRepository;


    @Autowired
    public GameService(@Qualifier("userService") UserService userService, @Qualifier("userRepository") UserRepository userRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.lobbyRepository = lobbyRepository;
    }

    public Game getGameById(long id, String token) {
        userService.authenticateUser(token);
        Game game = findGame(id);
        List<Player> players = game.getPlayers();
        for (Player player : players) {
            if (player.getToken().equals(token)) {
                return game;
            }

        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not in this game");
    }

    public Player getPlayerByToken(List<Player> players, String token) {
        for (Player player : players) {
            if (player.getToken().equals(token)) {
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not in this game");
    }

    private Game findGame(long gameId) {
        Game game = gameRepository.findById(gameId);
        if (game == null) {
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
        int amount = switch (move.getMove()) {
            case Fold -> {
                if (move.getAmount() != 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Fold with an Amount");
                }
                //set folded attribute to true, but he "remains" in game
                player.setFolded(true);
                if (game.getRaisePlayer() == player){
                    game.setRaisePlayer(null);
                }
                //no bet was made
                yield 0;
            }
            case Raise -> {
                if (player.getMoney() < move.getAmount()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot bet more money than you have!");
                }
                //if raise is legal, update the order and current bet and call raise
                if (move.getAmount() > game.getBet()) {
                    game.setBet(move.getAmount()); //set the current highest bet in game
                    int moneyLost = move.getAmount() - player.getLastRaiseAmount();
                    player.setMoney((player.getMoney() - moneyLost)); //remove money from user

                    player.setLastRaiseAmount(move.getAmount()); //save the last raised amount for call

                    //set the raise player so that it can be check in update game mehtod
                    game.setRaisePlayer(player);

                    yield move.getAmount(); //return bet amount
                }
                else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to bet higher than the current highest bet!");
            }
            case Check -> {
                if (move.getAmount() != 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Check with an Amount");
                }
                //only check if there was no bet made this betting round
                if (game.getBet() == 0) {
                    yield 0;
                }
                else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only check if there has been no bet made in this betting round");
                }

            }
            case Call -> {
                if (game.getBet() == 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only call if a Bet was made before");
                }
                if (move.getAmount() != 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Call with an Amount");
                }
                if (game.getBet() > player.getMoney()) {
                    //TODO All in call not implemented yet
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only call if you have enough money");
                }
                else { //if enough money, bet the current bet
                    int loss = game.getBet() - player.getLastRaiseAmount();
                    player.setMoney(player.getMoney() - loss); //p1 raises 100, p2 raises to 200, p1 calls --> only subtract (200-100 = 100) --> in total also 200
                    player.setLastRaiseAmount(player.getLastRaiseAmount()+ loss);
                    yield loss;
                }
            }

        };
        game.setsNextPlayerTurnIndex();
        return amount;


    }


    //updates game state (split from turn for easier testing)
    public void updateGame(long game_id, int bet) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        String nextPlayerUsername = game.getPlayers().get(game.getPlayerTurnIndex()).getUsername();

        if (bet > 0) {
            table.updateMoney(bet);
        }

        //check if all players except 1 folded
        if (playersfolded(game)) {
            winningCondition(game_id); //then game ends, call winning condition
            return;

        }
        //TODO BIG BLIND CAN PLAY AGAIN AFTER 1st round and ONLY after 1st round && 1st betting round
        //if(table.getCards().size() == 0 && bet == 25){}

        //check if current betting round is finished
        if ((game.getRaisePlayer() != null && Objects.equals(game.getRaisePlayer().getUsername(), nextPlayerUsername))) {
            //reset betting to 0 after 1 betting round

            game.setBet(0);
            for (Player player : game.getPlayers()) {
                player.setLastRaiseAmount(0);
            }

            //check if final round to end game
            if (table.getOpenCards().size() == 5) {
                winningCondition(game_id); //then game ends, call winning condition
                return;
            }
            table.updateOpenCards();
            setIndexToSBPlayer(game);
            //Sets the "Raiseplayer" to smallblind/ first person which hasent folded --> If noone raises so that the game still ends
            game.setRaisePlayer(setRaisePlayerCorrect(game));
        }
        if(game.getRaisePlayer() == null){
            game.setRaisePlayer(game.getPlayers().get(game.getPlayerTurnIndex()));
        }


    }


    public void authorize(String token, long id) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(id);

        //Check if valid inputs
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can access this when logged in!");
        }
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown game ID");
        }

        //will throw correct exception if not in player list
        Player player = game.getPlayerByUsername(username);

        if (game.getPlayers().get(game.getPlayerTurnIndex()) != player) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "It is not your turn!");
        }
    }


    public boolean playersfolded(Game game) {
        List<Player> players = game.getPlayers();
        int foldedPlayersCount = 0; // Initialize counter for folded players

        for (Player player : players) {
            if (player.isFolded()) {
                foldedPlayersCount++;
            }
        }
        return (foldedPlayersCount == (players.size() - 1));
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
        turn(gamePutDTOsmall, game.getId(), smallBlindPlayer.getToken());
        updateGame(game.getId(), smallBlind);

        GamePutDTO gamePutDTObig = new GamePutDTO();
        gamePutDTObig.setMove(Moves.Raise);
        gamePutDTObig.setAmount(bigBlind);

        authorize(bigBlindPlayer.getToken(), game.getId());
        turn(gamePutDTObig, game.getId(), bigBlindPlayer.getToken());
        updateGame(game.getId(), bigBlind);

    }

    public void setIndexToSBPlayer(Game game) {
        List<Player> players = game.getPlayers();

        Player smallBlindPlayer = game.getSmallBlindPlayer();

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(smallBlindPlayer)) {
                game.setPlayerTurnIndex(i);
                if (smallBlindPlayer.isFolded()) {
                    game.setsNextPlayerTurnIndex();
                }
                break;
            }
        }
    }
    public Player setRaisePlayerCorrect(Game game) {
        List<Player> players = game.getPlayers();
        Player smallBlindPlayer = game.getSmallBlindPlayer();

        if (!smallBlindPlayer.isFolded()) {
            return smallBlindPlayer;
        }

        int numberOfPlayers = players.size();
        int startIdx = players.indexOf(smallBlindPlayer);

        for (int i = 0; i < numberOfPlayers; i++) {
            int playerTurnIndex = (startIdx + i + 1) % numberOfPlayers;
            Player currentPlayer = players.get(playerTurnIndex);

            if (!currentPlayer.isFolded()) {
                return currentPlayer;
            }
        }
        return null;
    }


    public void deleteGame(Game game, int time) {
        ScheduledGameDelete scheduledGameDelete = new ScheduledGameDelete(gameRepository, lobbyRepository);
        scheduledGameDelete.scheduleGameDeletion(game, time);
    }


    //TODO destroy game class, add attributes to game to signal it's finished + players hand
    public void endGame(long game_id, PlayerHand winner) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        User user;

        //update user money
        for (Player player : game.getPlayers()) {
            user = userRepository.findByUsername(player.getUsername());
            if (player == winner.getPlayer()) {
                user.setMoney(player.getMoney() + table.getMoney());  //winner gets unused money back plus money on table
            }
            else {
                user.setMoney(player.getMoney()); //looser only gets unused money back
            }
        }

        //set winning player, name and cards of winning hand and then flag game as finished
        game.setHandCards(winner.getCards());
        game.setHandName(winner.getHand());
        game.setWinner(winner.getPlayer());
        game.setGameFinished(Boolean.TRUE);

        deleteGame(game, 60); //delete game after 1 minute
    }


    public void winningCondition(long game_id) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        PlayerHand winner = null;

        //find the best hand
        for (Player player : game.getPlayers()) {

            //ignore folded players
            if (player.isFolded()) {
                continue;
            }
            PlayerHand curHand = evaluateHand(player, table);

            curHand.getCards().sort(new Comparator<Card>() {
                @Override
                public int compare(Card c1, Card c2) {
                    return -Integer.compare(getValue(c1), getValue(c2));
                }
            });

            if (winner == null || handRank(curHand.getHand()) > handRank(winner.getHand())) {
                winner = curHand;
            }

            //TODO if two player have same hand (check further)
            else if (handRank(curHand.getHand()) == handRank(winner.getHand())) {
                if (getValue(curHand.getCards().get(0)) > getValue(winner.getCards().get(0))) {
                    winner = curHand;
                }
            }
        }
        endGame(game_id, winner);
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


        Hand[] hands = {Hand.ROYAL_FLUSH, Hand.STRAIGHT_FLUSH, Hand.FOUR_OF_A_KIND, Hand.FULL_HOUSE, Hand.FLUSH,
                Hand.STRAIGHT, Hand.THREE_OF_A_KIND, Hand.TWO_PAIR, Hand.ONE_PAIR};

        PlayerHand playerHand = null;

        //Iterate over the hands list and call the correct method
        for (Hand hand : hands) {
            playerHand = switch (hand) {
                case ROYAL_FLUSH -> royalFlush(cards, player);
                case STRAIGHT_FLUSH -> straightFlush(cards, player);
                case FOUR_OF_A_KIND -> fourCards(cards, player);
                case FULL_HOUSE -> fullHouse(cards, player);
                case FLUSH -> flush(cards, player);
                case STRAIGHT -> straight(cards, player);
                case THREE_OF_A_KIND -> threeOfKind(cards, player);
                case TWO_PAIR -> twoPair(cards, player);
                case ONE_PAIR -> pair(cards, player);
                default -> playerHand;
            };
            if (playerHand != null) {
                return playerHand;
            }
        }
        return highCard(cards, player);
    }

    public List<PlayerPublicGetDTO> settingPlayerInGameGetDTO(Game game, List<Player> players, PlayerPrivateGetDTO privatePlayer) {
        int iteration = 0;
        List<PlayerPublicGetDTO> playersInGame = new ArrayList<>();
        for (Player player : players) {
            PlayerPublicGetDTO playerPublicGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerPublicDTO(player);
            if (iteration == game.getPlayerTurnIndex()) {
                playerPublicGetDTO.setTurn(true);
                if (player.getId() == privatePlayer.getId()) {
                    privatePlayer.setTurn(true);
                }
            }
            else {
                playerPublicGetDTO.setTurn(false);
            }
            playersInGame.add(playerPublicGetDTO);
            iteration++;
        }
        return playersInGame;
    }

    public void addFinishedGamePlayers(GameGetDTO gameToReturn, Game game, List<Player> players) {

    List<PlayerPrivateGetDTO> notFoldedPlayers = new ArrayList<>();
        if(game.getGameFinished() == true) {
        gameToReturn.setWinner(DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(game.getWinner()));
        for(Player player : players) {
            if(!player.isFolded()) {
                notFoldedPlayers.add(DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(player));
            }
        }
        gameToReturn.setNotFoldedPlayers(notFoldedPlayers);
    }
        else {
        gameToReturn.setWinner(null);
        gameToReturn.setNotFoldedPlayers(notFoldedPlayers);
    }
        }
}
