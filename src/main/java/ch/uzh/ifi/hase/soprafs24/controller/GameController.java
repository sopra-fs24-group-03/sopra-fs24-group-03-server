package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Pot;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO.TablePublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Game getGameById(@PathVariable long id, @RequestHeader String token) {
        Game game = gameService.getGameById(id, token);
        GameGetDTO gameToReturn = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
       return game;

    }

    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Game makeMove(@RequestBody GamePutDTO move, @RequestHeader String token, @PathVariable long gameId) {
        gameService.authorize(token, gameId);
        int bet = gameService.turn(move, gameId, token);
        gameService.updateGame(gameId, bet);
        return gameService.getGameById(gameId, token);
    }


}
