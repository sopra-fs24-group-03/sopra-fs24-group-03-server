package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.ScheduledGameDelete;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
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
    public GameGetDTO getGameById(@PathVariable long id, @RequestHeader String token) {
        //get game
        Game game = gameService.getGameById(id, token);
        GameGetDTO gameToReturn = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        //getting ownPlayer and convert to privatPlayer and put in gametoreturn
        List<Player> players = new ArrayList<>();
        players = game.getPlayers();
        Player ownPlayer = gameService.getPlayerByToken(players, token);
        PlayerPrivateGetDTO privatePlayer = DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(ownPlayer);
        gameService.addFinishedGamePlayers(gameToReturn, game, players);
        gameToReturn.setPlayers(gameService.settingPlayerInGameGetDTO(game, players, privatePlayer));
        gameToReturn.setOwnPlayer(privatePlayer);
        return gameToReturn;

    }

    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void makeMove(@RequestBody GamePutDTO move, @RequestHeader String token, @PathVariable long gameId) {
        gameService.authorize(token, gameId);
        int bet = gameService.turn(move, gameId, token);
        gameService.updateGame(gameId, bet);
    }


    // just to test in postman not necessary must be deleted for M3
    @DeleteMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteGame(@RequestHeader String token, @PathVariable long gameId) {
        Game game = gameService.getGameById(gameId, token);
        gameService.deleteGame(game, 20);
    }
}
