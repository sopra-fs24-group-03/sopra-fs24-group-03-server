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


    private User user;
    private Lobby lobby;

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
        player.setMoney(2000);
        Mockito.when(game.getBet()).thenReturn(0);
        Mockito.when(move.getAmount()).thenReturn(100);
        Mockito.when(move.getMove()).thenReturn(Moves.Raise);
        Mockito.doNothing().when(game).setBet(Mockito.anyInt());
        //TODO Mockito.doNothing().when(game).updateOrder();

        int bet = gameService.turn(move, 1, "token");

        assertEquals(100, bet);
    }

    //TODO no assert?!
    @Test
    public void turn_raiseFail(){
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

    //TODO no assert?!
    @Test
    public void updateGame_success(){
        User user = new User();
        List<Player> players = new ArrayList<Player>();
        List<Card> cards = new ArrayList<Card>();

        players.add(player);
        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(player.getUsername()).thenReturn("username");
        Mockito.when(table.getCards()).thenReturn(cards);
        Mockito.when(game.getGameTable()).thenReturn(table);
        Mockito.doNothing().when(game).setsNextPlayerTurnIndex();

        gameService.updateGame(1, 0);
    }

    //TODO no assert?!
    @Test
    public void updateGame_success_withBet(){
        User user = new User();
        List<Player> players = new ArrayList<Player>();
        List<Card> cards = new ArrayList<Card>();

        players.add(player);
        user.setUsername("username");

        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        Mockito.when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        Mockito.when(game.getPlayers()).thenReturn(players);
        Mockito.when(player.getUsername()).thenReturn("username");
        Mockito.when(table.getCards()).thenReturn(cards);
        Mockito.when(game.getGameTable()).thenReturn(table);
        Mockito.doNothing().when(table).updateMoney(Mockito.anyInt());

        gameService.updateGame(1, 10);
    }


    @Test
    public void evaluateHand_flush(){
        List<Card> playerCards = Stream.of(
                new Card("1H", "image"),
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
        assertEquals("1H", hand.getCards().get(4).getCode());
    }

    @Test
    public void evaluateHand_highCard(){
        List<Card> playerCards = Stream.of(
                new Card("1H", "image"),
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
        assertEquals("3S", hand.getCards().get(4).getCode());
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
