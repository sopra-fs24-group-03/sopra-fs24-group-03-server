package ch.uzh.ifi.hase.soprafs24.service;

import antlr.Lookahead;
import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.assertj.core.internal.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class GameServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Game game;

    @Mock
    private GameTable table;

    @Mock
    private Player player;

    @Mock
    private GamePutDTO move;

    @Spy
    @InjectMocks
    private GameService gameService;


    @Test
    public void turn_foldSuccess(){
        User user = new User();

        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(move.getMove()).thenReturn(Moves.Fold);


        int bet = gameService.turn(move, 1, "token");

        assertEquals(0, bet);
    }

    @Test
    public void turn_raiseSuccess(){
        User user = new User();

        user.setUsername("username");


        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(0);
        Mockito.when(player.getMoney()).thenReturn(2000);
        Mockito.when(move.getAmount()).thenReturn(100);
        Mockito.when(move.getMove()).thenReturn(Moves.Raise);
        Mockito.doNothing().when(game).setBet(Mockito.anyInt());

        int bet = gameService.turn(move, 1, "token");
        assertEquals(100, bet);
    }
    @Test
    public void turn_raiseFailNotEnoughMoney(){
        User user = new User();

        user.setUsername("username");


        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(player.getMoney()).thenReturn(50);
        Mockito.when(move.getAmount()).thenReturn(100);
        Mockito.when(move.getMove()).thenReturn(Moves.Raise);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    @Test
    public void turn_raiseToLittleRaise(){
        User user = new User();

        user.setUsername("username");


        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(100);
        Mockito.when(move.getAmount()).thenReturn(0);
        Mockito.when(move.getMove()).thenReturn(Moves.Raise);


        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void turn_checkSuccess(){
        User user = new User();
        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(0);
        Mockito.when(move.getAmount()).thenReturn(0);
        Mockito.when(move.getMove()).thenReturn(Moves.Check);

        int bet = gameService.turn(move, 1, "token");
        assertEquals(0, bet);
    }
    @Test
    public void turn_checkFail(){
        User user = new User();
        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(100);
        Mockito.when(move.getAmount()).thenReturn(0);
        Mockito.when(move.getMove()).thenReturn(Moves.Check);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void turn_callSuccess(){
        User user = new User();

        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(100);
        Mockito.when(player.getMoney()).thenReturn(2000);
        Mockito.when(move.getAmount()).thenReturn(0);
        Mockito.when(player.getLastRaiseAmount()).thenReturn(0);
        Mockito.when(move.getMove()).thenReturn(Moves.Call);

        int bet = gameService.turn(move, 1, "token");
        assertEquals(100, bet);
    }
    @Test
    public void turn_callFail(){
        User user = new User();

        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getBet()).thenReturn(0);
        Mockito.when(move.getMove()).thenReturn(Moves.Call);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void authorize_Success(){
        User user = new User();
        user.setUsername("username");
        Player player = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(player);

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getPlayerTurnIndex()).thenReturn(0);

        //throws no error
        gameService.authorize("token", 1);

    }
    @Test
    public void authorize_Unauthorized(){
        User user = new User();
        user.setUsername("username");
        Player player = mock(Player.class);
        Player anotherPlayer = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(anotherPlayer);

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getPlayerTurnIndex()).thenReturn(1);

        //throws  error
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()->  gameService.authorize("token", 1)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());


    }



    @Test
    public void updateGame_UpdatesTableMoney() {
        long gameId = 1L;
        int betAmount = 100;
        Game game = mock(Game.class);
        GameTable table = mock(GameTable.class);

        Player currentPlayer = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(currentPlayer);

        Mockito.when(gameRepository.findById(gameId)).thenReturn(game);
        Mockito.when(game.getGameTable()).thenReturn(table);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(game.getPlayerTurnIndex()).thenReturn(0);
        Mockito.when(currentPlayer.getUsername()).thenReturn("username");
        Mockito.when(gameService.playersfolded(game)).thenReturn(false);


        // Explicitly block winningCondition from being called
        Mockito.doNothing().when(gameService).winningCondition(Mockito.anyLong());

        gameService.updateGame(gameId, betAmount);

        Mockito.verify(table, Mockito.times(1)).updateMoney(betAmount); // Check if table.updateMoney was called with the correct amount
    }


    @Test
    public void evaluateHand_flush(){
        List<Card> playerCards = Stream.of(
                new Card("AH", "image"),
                new Card("2H", "image")
        ).collect(Collectors.toList());

        List<Card> tableCards = Stream.of(
                new Card("3S", "image"),
                new Card("4H", "image"),
                new Card("5S", "image"),
                new Card("0H", "image"),
                new Card("AH", "image")
        ).collect(Collectors.toList());

        Mockito.when(player.getCards()).thenReturn(playerCards);
        Mockito.when(table.getCards()).thenReturn(tableCards);

        PlayerHand hand = gameService.evaluateHand(player, table);

        assertEquals(Hand.FLUSH, hand.getHand());
        assertEquals(5, hand.getCards().size());
        assertEquals("AH", hand.getCards().get(0).getCode());
        assertEquals("2H", hand.getCards().get(4).getCode());
    }

    @Test
    public void evaluateHand_highCard(){
        List<Card> playerCards = Stream.of(
                new Card("7H", "image"),
                new Card("2H", "image")
        ).collect(Collectors.toList());

        List<Card> tableCards = Stream.of(
                new Card("3S", "image"),
                new Card("4D", "image"),
                new Card("KS", "image"),
                new Card("0H", "image"),
                new Card("AH", "image")
        ).collect(Collectors.toList());

        Mockito.when(player.getCards()).thenReturn(playerCards);
        Mockito.when(table.getCards()).thenReturn(tableCards);

        PlayerHand hand = gameService.evaluateHand(player, table);

        assertEquals(Hand.HIGH_CARD, hand.getHand());
        assertEquals(5, hand.getCards().size());
        assertEquals("AH", hand.getCards().get(0).getCode());
        assertEquals("4D", hand.getCards().get(4).getCode());
    }


    @Test
    public void winningCondition(){
        //Setup
        List<Card> cards = new ArrayList<>(){{
            add(new Card("3S", "image"));
            add(new Card("4D", "image"));
            add(new Card("KS", "image"));
            add(new Card("0H", "image"));
            add(new Card("AH", "image"));
        }};

        Player player1 = new Player(game, "username1", 1, "token1", cards);
        Player player2 = new Player(game, "username2", 1, "token2", cards);

        PlayerHand playerHand1 = new PlayerHand();
        PlayerHand playerHand2 = new PlayerHand();
        playerHand1.setHand(Hand.STRAIGHT_FLUSH);
        playerHand2.setHand(Hand.ROYAL_FLUSH);
        playerHand1.setCards(cards);
        playerHand2.setCards(cards);

        List<Player> players = Stream.of(player1, player2).toList();

        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getGameTable()).thenReturn(table);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.doReturn(playerHand1).doReturn(playerHand2).when(gameService).evaluateHand(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(gameService).endGame(Mockito.anyLong(), Mockito.any());

        //method call
        gameService.winningCondition(1L);

        
        //verify endGame is called with the correct parameters
        Mockito.verify(gameService).endGame(eq(1L), eq(playerHand2));
    }
}
