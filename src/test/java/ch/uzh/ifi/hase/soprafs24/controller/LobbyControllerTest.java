package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

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
                .andExpect(jsonPath("$.lobbyLeaderUsername").value(user.getUsername()))
                .andExpect(jsonPath("$.lobbyUsernames.length()").value(1))
                .andExpect(jsonPath("$.lobbyUsernames[0]").value(user.getUsername()));

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
                .andExpect(jsonPath("$.lobbyLeaderUsername").value(user.getUsername()))
                .andExpect(jsonPath("$.lobbyUsernames.length()").value(1))
                .andExpect(jsonPath("$.lobbyUsernames[0]").value(user.getUsername()));
    }
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








}
