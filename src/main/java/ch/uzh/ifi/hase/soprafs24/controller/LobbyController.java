package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO.LobbyGetDTO;
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

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestHeader String token) {
        Lobby newLobby =lobbyService.createLobby(token);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(newLobby);

    }

    @PostMapping("/lobbies/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createGame(@RequestHeader String token, @PathVariable long id){
        Game game = lobbyService.startGame(token, id);
    }

    @GetMapping("/lobbies/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyByID(@PathVariable long id, @RequestHeader String token){
        Lobby lobby = lobbyService.getLobbyById(id, token);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @PutMapping("/lobbies/{id}")
    @ResponseStatus(HttpStatus.OK )
    @ResponseBody
    public LobbyGetDTO joinLobbyById(@PathVariable long id, @RequestHeader String token){
        Lobby lobby = lobbyService.joinLobbyById( id, token);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);

    }

    @DeleteMapping("/lobbies")
    @ResponseStatus(HttpStatus.ACCEPTED )
    @ResponseBody
    public void removeUserFromLobbyById(@RequestHeader String token){
        lobbyService.removeUserFromLobby(token);

    }
}
