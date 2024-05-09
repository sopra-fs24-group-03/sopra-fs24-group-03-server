package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.*;
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
import ch.uzh.ifi.hase.soprafs24.rest.dto.PotDTO.PotPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static ch.uzh.ifi.hase.soprafs24.constant.Moves.*;
import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;
import static ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand.*;


@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LobbyRepository lobbyRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentFoldTask;




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



    private void setMoveInGameTable(GameTable gameTable, Moves move,int amount, long playerId){
        if(gameTable != null) {
            gameTable.setLastMove(move);
            gameTable.setLastMoveAmount(amount);
            gameTable.setPlayerIdOfLastMove(playerId);
        }
    }

    //method to make moves
    public int turn(GamePutDTO move, long game_id, String token) {
        String username = userRepository.findByToken(token).getUsername();
        Game game = gameRepository.findById(game_id);
        GameTable gameTable = game.getGameTable();
        Player player = game.getPlayerByUsername(username);


        //call the correct method and return the amount bet, if nothing is bet return 0
        int amount = switch (move.getMove()) {
            case Fold -> {
                if (move.getAmount() != 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Fold with an Amount");
                }
                //set folded attribute to true, but he "remains" in game
                setMoveInGameTable(gameTable,Fold,0,player.getId());
                player.setFolded(true);
                if (game.getRaisePlayer() == player) {
                    game.setRaisePlayer(null);
                }
                //no bet was made
                yield 0;
            }
            case Raise -> {
                if (player.getMoney() < move.getAmount() - player.getLastRaiseAmount()) {
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

                    yield moneyLost; //return bet amount
                }
                else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to bet higher than the current highest bet!");
            }
            case Check -> {
                if (move.getAmount() != 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot Check with an Amount");
                }
                //only check if there was no bet made this betting round
                if (game.getBet()-player.getLastRaiseAmount() == 0) {
                    setMoveInGameTable(gameTable,Check,0,player.getId());
                    yield 0;
                }
                else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only check if there has been no bet made in this betting round");
                }

            }
            case Call -> {
                if (game.getBet() - player.getLastRaiseAmount()== 0) {
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
                    setMoveInGameTable(gameTable,Call,0,player.getId());

                    gameTable.setTotalTableBettingInCurrentRound(gameTable.getTotalTableBettingInCurrentRound() +playerMoney);
                    player.setTotalBettingInCurrentRound(player.getTotalBettingInCurrentRound() + playerMoney);

                    if (game.getRaisePlayer() == player) {
                        game.setRaisePlayer(null);
                    }
                    yield playerMoney;


                }
                else { //if enough money, bet the current bet
                    int loss = game.getBet() - player.getLastRaiseAmount();
                    player.setMoney(player.getMoney() - loss); //p1 raises 100, p2 raises to 200, p1 calls --> only subtract (200-100 = 100) --> in total also 200
                    player.setLastRaiseAmount(player.getLastRaiseAmount() + loss);
                    setMoveInGameTable(gameTable,Call,0,player.getId());

                    //set the amounts of current betting round
                    gameTable.setTotalTableBettingInCurrentRound(gameTable.getTotalTableBettingInCurrentRound() + loss);
                    player.setTotalBettingInCurrentRound(player.getTotalBettingInCurrentRound()+loss);
                    //return amount to be updated in table
                    yield loss;
                }
            }

        };
        //For very first betting round, so that Bigblind can play again, it sets the raiseplayer to 1 in front of bigblind so that he can make another move and then new betting round starts
        if(game.getGameTable().getOpenCards().size()==0 && game.getRaisePlayer()==null && move.getMove() != Fold && !player.isAllIn()){
            game.setRaisePlayer(game.getPlayers().get(game.getPlayerTurnIndex()));

        }
        game.setsNextPlayerTurnIndex();
        List<Player> players = game.getPlayers();
        int turn = game.getPlayerTurnIndex();
        //startTimer(game_id, players.get(turn).getToken());
        return amount;
    }




    //updates game state (split from turn for easier testing)
    public void updateGame(long game_id, int bet) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        String nextPlayerUsername = game.getPlayers().get(game.getPlayerTurnIndex()).getUsername();
        List<Player> players = game.getPlayers();

        if (bet > 0) {
            table.getPotByName("mainPot").updateMoney(bet);
        }

        //check if all players except 1 folded
        if (playersFolded(game)) {
            endGame(game_id); //then game ends, call winning condition
            return;
        }
        //checks if ALL players are folded OR all In --> end game
        if (playersFoldedOrAllIn(game)) {
            endGame(game_id); //then game ends, call winning condition
            return;
        }



        //check if current betting round is finished
        if ((game.getRaisePlayer() != null && Objects.equals(game.getRaisePlayer().getUsername(), nextPlayerUsername))) {
            List<Player> allInPlayers = filterPlayersAllIn(players);
            players.sort(Comparator.comparing(Player::getTotalBettingInCurrentRound));
            List<Player> notFoldedPlayers = filterPlayersNotFolded(players);

            if (!allInPlayers.isEmpty()) {
                calculatePots(game,allInPlayers, notFoldedPlayers, table.getTotalTableBettingInCurrentRound());
            }

            //reset betting to 0 after a betting round
            game.setBet(0);
            table.setTotalTableBettingInCurrentRound(0);
            for (Player player : game.getPlayers()) {
                player.setLastRaiseAmount(0);
                player.setTotalBettingInCurrentRound(0);
            }

            //check if final round to end game
            if (table.getOpenCards().size() == 5 || playersAllIn(notFoldedPlayers)) {
                endGame(game_id); //then game ends, call winning condition
                return;
            }
            table.updateOpenCards();
            setIndexToSBPlayer(game);
            //Sets the "Raiseplayer" to smallblind/ first person which hasent folded --> If noone raises so that the game still ends
            game.setRaisePlayer(setRaisePlayerCorrect(game));
        }
        if (game.getRaisePlayer() == null) {
            game.setRaisePlayer(game.getPlayers().get(game.getPlayerTurnIndex()));
        }
    }


    public void authorize(String token, long id) {
        User user = userRepository.findByToken(token);
        Game game = gameRepository.findById(id);

        //Check if valid inputs
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can access this when logged in!");
        }
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown game ID");
        }

        //will throw correct exception if not in player list
        Player player = game.getPlayerByUsername(user.getUsername());

        if (game.getPlayers().get(game.getPlayerTurnIndex()) != player) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "It is not your turn!");
        }
    }


    public List<Player> filterPlayersAllIn(List<Player> players) {
        List<Player> qualifiedPlayers = new ArrayList<>();

        for (Player player : players) {
            if (player.isAllIn() && player.getTotalBettingInCurrentRound() != 0) {
                qualifiedPlayers.add(player);
            }
        }

        return  qualifiedPlayers;
    }
    public List<Player> filterPlayersNotFolded(List<Player> players) {
        List<Player> qualifiedPlayers = new ArrayList<>();

        for (Player player : players) {
            if (!player.isFolded()) {
                qualifiedPlayers.add(player);
            }
        }

        return qualifiedPlayers;
    }

    public boolean playersAllIn (List<Player> notFoldedPlayers){
        int finishedPlayersCount = 0; // Initialize counter for folded players

        for (Player player : notFoldedPlayers) {
            if (player.isAllIn()) {
                finishedPlayersCount++;
            }
        }
        return (finishedPlayersCount >= (notFoldedPlayers.size() - 1));

    }

    public void calculatePots(Game game, List<Player> allInPlayersOrdered, List<Player> allNotFoldedPlayers, int totalBetting){
        int amountOfMinimumAllIn = 0;
        int amountForPreviousPot = 0;
        int potNumber = 1;
        int money = 0;
        int numberOfPlayers = allNotFoldedPlayers.size();
        List<Pot> newSidePots = new ArrayList<>();
        for (Player player: allInPlayersOrdered){
            if(amountOfMinimumAllIn == player.getTotalBettingInCurrentRound()){
                numberOfPlayers -= 1;
                continue;
            }
            amountOfMinimumAllIn = player.getTotalBettingInCurrentRound();
            money = (player.getTotalBettingInCurrentRound() - amountForPreviousPot) * numberOfPlayers ;
            amountForPreviousPot = amountOfMinimumAllIn;
            numberOfPlayers -= 1;
            String name = "sidepot" + potNumber;
            Pot sidepot = new Pot(money, name);
            newSidePots.add(sidepot);
        }
        List<Pot> oldPots = game.getGameTable().getPots();
        for (Pot sidePot : newSidePots){
            oldPots.add(sidePot);
        }
        Pot mainPot = oldPots.get(0);
        int mainMoney = mainPot.getMoney();
        for (Pot pot : newSidePots){
            totalBetting -= pot.getMoney();
        }
        mainMoney += totalBetting;
        mainPot.setMoney(mainMoney);
        for (Player player : allInPlayersOrdered) {
            System.out.println("Username: " + player.getUsername() + ", Total Betting: " + player.getTotalBettingInCurrentRound());
        }
    }
    public boolean playersFolded(Game game) {
        List<Player> players = game.getPlayers();
        int finishedPlayersCount = 0; // Initialize counter for folded players

        for (Player player : players) {
            if (player.isFolded()) {
                finishedPlayersCount++;
            }
        }
        return (finishedPlayersCount == (players.size() - 1));
    }
    public boolean playersFoldedOrAllIn(Game game) {
        List<Player> players = game.getPlayers();
        int finishedPlayersCount = 0; // Initialize counter for folded players

        for (Player player : players) {
            if (player.isFolded() || player.isAllIn()) {
                finishedPlayersCount++;
            }
        }
        return (finishedPlayersCount == (players.size()));
    }


    public void setIndexToSBPlayer(Game game) {
        List<Player> players = game.getPlayers();

        Player smallBlindPlayer = game.getSmallBlindPlayer();

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(smallBlindPlayer)) {
                game.setPlayerTurnIndex(i);
                if (smallBlindPlayer.isFolded() || smallBlindPlayer.isAllIn()) {
                    game.setsNextPlayerTurnIndex();
                }
                break;
            }
        }
    }

    public Player setRaisePlayerCorrect(Game game) {
        List<Player> players = game.getPlayers();
        Player smallBlindPlayer = game.getSmallBlindPlayer();

        if (!smallBlindPlayer.isFolded() && !smallBlindPlayer.isAllIn()) {
            return smallBlindPlayer;
        }

        int numberOfPlayers = players.size();
        int startIdx = players.indexOf(smallBlindPlayer);

        for (int i = 0; i < numberOfPlayers; i++) {
            int playerTurnIndex = (startIdx + i + 1) % numberOfPlayers;
            Player currentPlayer = players.get(playerTurnIndex);

            if (!currentPlayer.isFolded() && !currentPlayer.isAllIn()) {
                return currentPlayer;
            }
        }
        return null;
    }


    public void deleteGame(Game game, int time) {
        ScheduledGameDelete scheduledGameDelete = new ScheduledGameDelete(gameRepository, lobbyRepository);
        scheduledGameDelete.scheduleGameDeletion(game, time);
    }

    public void endGame(long game_id) {
        Game game = gameRepository.findById(game_id);
        GameTable table = game.getGameTable();
        User user;
        List<PlayerHand> overallWinner = new ArrayList<>();



        //return unused money
        for(Player player : game.getPlayers()) {
            user = userRepository.findByUsername(player.getUsername());

            //set profit to send to frontend
            player.setProfit(player.getMoney()-user.getMoney());
            user.setMoney(player.getMoney());
        }

        //split the pots among the winners
        for(Pot pot : table.getPots()){
            List<PlayerHand> winners = winningCondition(pot);

            //this is to evaluate the best hand
            if(Objects.equals(pot.getName(), "mainPot")){
                overallWinner = winners;
            }

            for (PlayerHand winner : winners) {
                user = userRepository.findByUsername(winner.getPlayer().getUsername());
                int reward = (int) Math.ceil((double) pot.getMoney() / winners.size());

                //add reward to profit
                winner.getPlayer().setProfit(winner.getPlayer().getProfit() + reward);
                user.setMoney(reward + user.getMoney());
            }
        }

        //update user tries
        for(Player player : game.getPlayers()) {
            user = userRepository.findByUsername(player.getUsername());
            user.updateUser();
        }

        //set winning player, name and cards of winning hand and then flag game as finished
        game.setWinner(overallWinner);
        game.setGameFinished(true);
        deleteGame(game, 10);
    }


    public List<PlayerHand> winningCondition(Pot pot) {
        GameTable table = pot.getGameTable();
        //has to be list to accommodate for potential draws
        List<PlayerHand> winner = new ArrayList<>();

        //find the best hand
        for (Player player : pot.getEligiblePlayers()) {

            //ignore folded players
            if (player.isFolded()) {
                continue;
            }
            PlayerHand curHand = evaluateHand(player, table);

            //the curHand has the better hand, update winner
            if (winner.isEmpty() || handRank(curHand.getHand()) > handRank(winner.get(0).getHand())) {
                winner.clear();
                winner.add(curHand);
            }

            //the hands are equal, compare the cards
            else if (handRank(curHand.getHand()) == handRank(winner.get(0).getHand())) {

                //iterate over all cards to compare and resolve the draw
                for (int i = 0; i < winner.get(0).getCards().size(); i++) {

                    //the winner has the better cards, break out of the loop
                    if (getValue(winner.get(0).getCards().get(i)) > getValue(curHand.getCards().get(i))) {
                        break;
                    }

                    //the curHand has the better cards, update winner and break out of the loop
                    else if (getValue(winner.get(0).getCards().get(i)) < getValue(curHand.getCards().get(i))) {
                        winner.clear();
                        winner.add(curHand);
                        break;
                    }

                    //all cards have been compared and neither side has the better hand, the 2 players have a draw
                    else if (i == winner.get(0).getCards().size() - 1) {
                        winner.add(curHand);
                    }
                }
            }
        }
        return winner;
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
                if (Objects.equals(player.getId(), privatePlayer.getId())) {
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
        if (game.getGameFinished()) {
            for (Player winner : game.getWinner()) {
                gameToReturn.addWinner(DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(winner));
            }
            for (Player player : players) {
                if (!player.isFolded()) {
                    notFoldedPlayers.add(DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(player));
                }
                else {
                    PlayerPrivateGetDTO playerWithoutCards = DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(player);
                    playerWithoutCards.deleteCardsImage();
                    notFoldedPlayers.add(playerWithoutCards);
                }
            }
            game.setGameFinished(true);
            gameToReturn.setNotFoldedPlayers(notFoldedPlayers);
        }
        else {
            gameToReturn.noWinner();
            gameToReturn.setNotFoldedPlayers(notFoldedPlayers);
        }
    }
    public List<PotPublicGetDTO> settingPotsInGameTable(List<Pot> pots) {
        List<PotPublicGetDTO> potPublicGetDTO = new ArrayList<>();
        for(Pot pot : pots) {
            potPublicGetDTO.add(DTOMapper.INSTANCE.convertEntityToPotPublicGetDTO(pot));
        }
        return potPublicGetDTO;
    }
}

