package ch.uzh.ifi.hase.soprafs24.controller;



import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

}
