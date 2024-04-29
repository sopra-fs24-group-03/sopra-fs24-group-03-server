package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;
    @MockBean
    private UserService userService;

    @Test
    public void userCreatesLobbySuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        Lobby lobby = new Lobby();
        lobby.setId(2L);
        lobby.setLobbyLeader(user);
        lobby.addUserToLobby(user);


        Mockito.when(lobbyService.createLobby(Mockito.anyString())).thenReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lobbyLeader.username").value(user.getUsername()))
                .andExpect(jsonPath("$.lobbyUsers.size()").value(1));

    }
    @Test
    public void getLobbyByIdSuccess() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        Lobby lobby = new Lobby();
        lobby.setId(2L);
        lobby.setLobbyLeader(user);
        lobby.addUserToLobby(user);


        Mockito.when(lobbyService.getLobbyById(Mockito.anyLong(), Mockito.anyString())).thenReturn(lobby);


        MockHttpServletRequestBuilder getRequest = get("/lobbies/{id}", lobby.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");

        // Verify response
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lobby.getId()))
                .andExpect(jsonPath("$.lobbyLeader.username").value(user.getUsername()))
                .andExpect(jsonPath("$.lobbyUsers.size()").value(1));}
    @Test
    public void getLobbyByIdUnauthorized() throws Exception {

        Mockito.when(lobbyService.getLobbyById(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));


        MockHttpServletRequestBuilder getRequest = get("/lobbies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");


        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());

    }
    @Test
    public void joinLobbyByIdSuccess() throws Exception {

        Lobby lobby = new Lobby();
        Mockito.when(lobbyService.joinLobbyById(Mockito.anyLong(), Mockito.anyString())).thenReturn(lobby);


        MockHttpServletRequestBuilder putRequest = put("/lobbies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");


        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void joinLobbyByIdBadRequest() throws Exception {

        Mockito.when(lobbyService.joinLobbyById(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // Perform PUT request
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");

        // Verify response
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeUserFromLobbyByIdSuccess() throws Exception {
        Mockito.doNothing().when(lobbyService).removeUserFromLobby(Mockito.anyString());

        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies")
                .header("token", "token");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isAccepted());
    }
    @Test
    public void removeUserFromLobbyByIdBadRequest() throws Exception {

        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(lobbyService).removeUserFromLobby(Mockito.anyString());


        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies")
                .header("token", "token");

        // Verify response
        mockMvc.perform(deleteRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void createGameWithLobbyIdSuccess() throws Exception {

        User user1 = new User();
        ArrayList<User> Userlist = new ArrayList<User>();
        Userlist.add(user1);

        Lobby lobby = new Lobby();
        Game game = new Game(Userlist);

        Mockito.when(lobbyService.startGame(Mockito.anyString(), Mockito.anyLong())).thenReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/lobbies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
                //.andExpect(jsonPath("$.id").value(game.getId()));

    }


    @Test
    public void createGameWithLobbyIdBadRequest() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(lobbyService).startGame(Mockito.anyString(), Mockito.anyLong());

        MockHttpServletRequestBuilder postRequest = post("/lobbies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void kickUserFromLobby_Success() throws Exception {
        long lobbyId = 1L;
        long userToDeleteId = 2L;
        String token = "Bearer valid-token";

        // Assume that the method will succeed and thus not throw an exception
        Mockito.doNothing().when(lobbyService).kickUserOutOfLobby(token, userToDeleteId, lobbyId);

        MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/{lobbyId}/remove/{userToDeleteId}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "token");
        // Perform the DELETE request to the controller
        mockMvc.perform(deleteRequest)
                .andExpect(status().isAccepted());  // Ensure that the status is as expected (HTTP 202 ACCEPTED)
    }





}
