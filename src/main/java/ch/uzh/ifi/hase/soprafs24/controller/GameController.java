package ch.uzh.ifi.hase.soprafs24.controller;



import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Game getGameByID (@PathVariable long id, @RequestHeader String token){
        Game game = gameService.getGameById(id, token);
        return game;

    }

    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Game makeMove(@RequestBody GamePutDTO move, @RequestHeader String token, @PathVariable long gameId){
        gameService.authorize(token, gameId);
        int bet = gameService.turn(move, gameId, token);
        gameService.updateGame(gameId, bet);
        Game game = gameService.getGameById(gameId, token);
        return game;
    }

}
