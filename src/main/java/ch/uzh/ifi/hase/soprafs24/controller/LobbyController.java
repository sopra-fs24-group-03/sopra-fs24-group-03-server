package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Lobby;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {
    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;

    }

    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyPostDTO createLobby(@RequestHeader String token) {
        Lobby newLobby =lobbyService.createLobby(token);
        return DTOMapper.INSTANCE.convertEntityToLobbyPostDTO(newLobby);

    }
    @GetMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyByID(@PathVariable long id, @RequestHeader String token){
        Lobby lobby = lobbyService.getLobbyById(id, token);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }
}
