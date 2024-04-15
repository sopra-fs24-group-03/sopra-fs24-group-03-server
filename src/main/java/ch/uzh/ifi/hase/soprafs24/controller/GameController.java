package ch.uzh.ifi.hase.soprafs24.controller;



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

    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void makeMove(@RequestBody GamePutDTO move, @RequestHeader String token, @PathVariable long gameId){
        gameService.authorize(token, gameId);
        int bet = gameService.turn(move, gameId, token);
        gameService.updateGame(gameId, bet, token);
    }

}
