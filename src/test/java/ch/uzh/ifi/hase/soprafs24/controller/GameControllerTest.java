package ch.uzh.ifi.hase.soprafs24.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        Mockito.doNothing().when(gameService).turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}" )
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void makeMove_success_withoutAmount() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.doNothing().when(gameService).turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Check\"}" )
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void makeMove_authorize_fail() throws Exception {
        //setup
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.doNothing().when(gameService).turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}" )
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

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"Raise\", \"amount\": 100}" )
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void makeMove_unknownMove() throws Exception {
        //setup
        Mockito.doNothing().when(gameService).authorize(Mockito.anyString(), Mockito.anyLong());
        Mockito.doNothing().when(gameService).turn(Mockito.any(), Mockito.anyLong(), Mockito.anyString());

        //when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"move\": \"UnknownMove\", \"amount\": 100}" )
                .header("token", "token");


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }

}
