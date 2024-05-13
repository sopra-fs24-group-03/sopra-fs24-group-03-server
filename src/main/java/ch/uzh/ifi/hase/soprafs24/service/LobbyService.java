package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.rotate;


@Service
@Transactional
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final GameService gameService;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, @Qualifier("userService") UserService userService, @Qualifier("gameService") GameService gameService) {
        this.lobbyRepository = lobbyRepository;
        this.gameService = gameService;
        this.userService = userService;
    }

    public Lobby createLobby(String token) {
        userService.authenticateUser(token);
        User creatorUser = userService.getUserByToken(token);

        // Check if the user is not already in a lobby
        if (creatorUser.getLobby() == null) {
            Lobby newLobby = new Lobby();
            creatorUser.setLobby(newLobby);
            newLobby.setLobbyLeader(creatorUser);
            newLobby.addUserToLobby(creatorUser);
            newLobby = lobbyRepository.save(newLobby);
            lobbyRepository.flush();
            log.debug("Created new lobby {} for User: {}", newLobby, creatorUser);
            return newLobby;
        }
        else {
            // Throw an exception if the user is already in a lobby
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in a lobby!");
        }
    }

    public Lobby getLobbyById(long id, String token) {
        userService.authenticateUser(token);
        User user = userService.getUserByToken(token);
        Lobby lobby = findLobby(id);

        if (user.getLobby() != null && user.getLobby().getId() == id) {
            // Return the lobby if the user is in it
            return lobby;
        }
        else {
            // Throw an exception if the user is not in the lobby
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users that are in the specified lobby can access its information!");
        }
    }

    public Lobby joinLobbyById(long id, String token) {
        userService.authenticateUser(token);
        User user = userService.getUserByToken(token);
        Lobby lobby = findLobby(id);

        if (user.getLobby() == null) {
            if (lobby.getLobbyusers().size() < 7) {
                if(lobby.getGame() == null){
                    user.setLobby(lobby);
                    lobby.addUserToLobby(user);
                    return lobby;
                }else{throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only join a Lobby when there is no game running in it!");}

            }
            else {// Throw an exception if the lobby is full
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Lobby is full!");
            }
        }
        else {
            // Throw an exception if the user is already in a lobby
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already in a lobby!");
        }
    }

    public void removeUserFromLobby(String token) {
        userService.authenticateUser(token);
        User user = userService.getUserByToken(token);
        Lobby lobby = user.getLobby();

        if (lobby != null) {
            if(lobby.getGame() == null){
            // Remove the user from the lobby
            user.setLobby(null);
            lobby.removeUserFromLobby(user);

            // Check if the lobby has no users left
            if (lobby.getLobbyusers().isEmpty()) {
                // Delete the lobby from the database
                lobbyRepository.delete(lobby);
                lobbyRepository.flush();
                return;
            }

            // Check if the user leaving is the lobby leader
            if (lobby.getLobbyLeader().getId().equals(user.getId())) {
                // Assign lobby leader role to another user in the lobby
                List<User> lobbyUsers = lobby.getLobbyusers();
                User newLeader = lobbyUsers.iterator().next();
                lobby.setLobbyLeader(newLeader);
            }
            return;
            }
            else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only join a Lobby when there is no game running in it!");}
        }
        else {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in a Lobby!");}
    }


    //start a new Game in lobby
    public Game startGame(String token, long lobbyId) {
        Lobby lobby = findLobby(lobbyId);
        authenticateLeader(token, lobby);

        //throw exception if game already running
        if (lobby.getGame() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "lobby already has running game!");
        }
        List<User> users = lobby.getLobbyusers();

        //checks for enough users
        if (users.size() >= 2) {
            //rotate the lobbylist by correct amount so that SB and BB players change
            rotate(users, -lobby.getIndexStartingPlayer());
            lobby.setIndexStartingPlayer(lobby.getIndexStartingPlayer()+1);

            Game game = new Game(users);
            lobby.setGame(game);
            game.setId(lobby.getId());

            log.info("created game {}", game);
            lobbyRepository.save(lobby);
            lobbyRepository.flush();
            return game;
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough players");

    }

    //Check if provided token belongs to lobby leader, throws exception if not, does nothing otherwise.
    private void authenticateLeader(String token, Lobby lobby) {
        userService.authenticateUser(token);

        //compare provided token and leader token
        if (!Objects.equals(lobby.getLobbyLeader().getToken(), token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the lobby leader can do this!");
        }
    }


    //returns requested lobby, if lobby doesn't exist throws exception
    public Lobby findLobby(long lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown lobby");
        }
        return lobby;
    }

    public void kickUserOutOfLobby(String tokenOfDeleter, long kickedUserId, long lobbyId) {
        Lobby lobby = findLobby(lobbyId);
        // checks if the player who wants to delete someone is the Lobbyleader
        if(userService.getUserByToken(tokenOfDeleter).equals(lobby.getLobbyLeader())) {
            if(userService.getUserByToken(tokenOfDeleter) == userService.getUserById(kickedUserId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot kick yourself");
            }
            User userToKick = userService.getUserById(kickedUserId);
            userToKick.setLobby(null);
            lobby.removeUserFromLobby(userToKick);
//            lobbyRepository.flush();
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the lobby leader can kick other players");
        }
    }


}