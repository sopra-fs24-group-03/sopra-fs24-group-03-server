package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledGameDelete {
    private final GameRepository gameRepository;
    private final LobbyRepository lobbyRepository;

    public ScheduledGameDelete(GameRepository gameRepository, LobbyRepository lobbyRepository) {
        this.gameRepository = gameRepository;
        this.lobbyRepository = lobbyRepository;
    }

    public void scheduleGameDeletion(Game game, int delayInSeconds) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> deleteGame(game), delayInSeconds, TimeUnit.SECONDS);
        executor.shutdown();
    }

    private Lobby findLobby(long lobbyId){
        Lobby lobby = lobbyRepository.findById(lobbyId);
        if(lobby == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown lobby");
        }
        return lobby;
    }

    private void updateUserMoney(Lobby lobby, Game game) {
        for(Player player : game.getPlayers()) {
            for(User user : lobby.getLobbyusers()) {
                if(user.getToken() == player.getToken()) {
                    user.setMoney(player.getMoney());
                }
            }
        }

    }
    public void deleteGame(Game game) {
        Lobby lobby = findLobby(game.getId());
        lobby.setGameToNull();
        updateUserMoney(lobby, game);
        lobbyRepository.save(lobby);
        gameRepository.delete(game);
        gameRepository.flush();
    }

}
