package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

        gameService.updateGame(1, 0, "token");
    }

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

        gameService.updateGame(1, 10, "token");
    }


}
