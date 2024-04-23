package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void makeMove_success_withAmount() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(gameService.turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
        Mockito.doNothing().when(gameService).updateGame(Mockito.anyLong(), Mockito.anyInt());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}")
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void makeMove_success_withoutAmount() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(gameService.turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
        Mockito.doNothing().when(gameService).updateGame(Mockito.anyLong(), Mockito.anyInt());


        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Check\"}")
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void makeMove_authorize_fail() throws Exception {
        //setup
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(gameService.turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
        Mockito.doNothing().when(gameService).updateGame(Mockito.anyLong(), Mockito.anyInt());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}")
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void makeMove_turn_fail() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(gameService).turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString());
        Mockito.doNothing().when(gameService).updateGame(Mockito.anyLong(), Mockito.anyInt());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}")
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void makeMove_unknownMove() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(gameService.turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
        Mockito.doNothing().when(gameService).updateGame(Mockito.anyLong(), Mockito.anyInt());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"UnknownMove\", \"amount\": 100}")
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }
    // test not finished
    //.andExpect(jsonPath("$.ownPlayer.id").value(0)); not working
    @Test
    public void getGameByIdSuccess() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("testUsername1");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);

        Game game = new Game(users);
        game.setId(1L);
        game.setPlayerTurnIndex(0);
        String token = "validToken";

        Card card1 = new Card("1", "2");
        Card card2 = new Card("3", "4");
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);

        Player player1 = new Player(game, "PlayerOne", 2000, "validToken1", cards);
        player1.setId(2L);

        Player player2 = new Player(game, "PlayerTwo", 2000, "validToken2", cards);
        player1.setId(3L);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game.setPlayers(players);


        Mockito.when(gameService.getGameById(game.getId(), token)).thenReturn(game);
        Mockito.when(gameService.getPlayerByToken(players, token)).thenReturn(player1);
        //Mockito.when(game.getPlayers()).thenReturn(players);


        MockHttpServletRequestBuilder getRequest = get("/games/{id}", game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "validToken");

        // Verify response
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                //.andExpect(jsonPath("$.players[0].username").value("PlayerOne"))
                .andExpect(jsonPath("$.currentBet").value(0));


    }

    @Test
    public void getGameByIdUnauthorized() throws Exception {

        Mockito.when(gameService.getGameById(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        MockHttpServletRequestBuilder getRequest = get("/games/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "validToken");


        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());

    }
}
