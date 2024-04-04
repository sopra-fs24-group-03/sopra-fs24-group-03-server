package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;


@Service
@Transactional
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, @Qualifier("userRepository") UserRepository userRepository, @Qualifier("userService") UserService userService){
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Lobby createLobby(String token) {
        userService.authenticateUser(token);
        User creatorUser = userRepository.findByToken(token);

        // Check if the user is not already in a lobby
        if (creatorUser.getLobby() == null) {
            Lobby newLobby = new Lobby();
            creatorUser.setLobby(newLobby);
            newLobby.setLobbyLeader(creatorUser);
            newLobby.addUserToLobby(creatorUser);
            newLobby = lobbyRepository.save(newLobby);
            lobbyRepository.flush();
            log.debug("Created Information for User: {}", newLobby);
            return newLobby;
        }
        else {
            // Throw an exception if the user is already in a lobby
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in a lobby!");
        }
    }

    public Lobby getLobbyById(long id, String token) {
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = getLobby(id);

        if (user.getLobby() != null && user.getLobby().getId() == id) {
            // Return the lobby if the user is in it
            return lobby;
        } else {
            // Throw an exception if the user is not in the lobby
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users that are in the specified lobby can access its information!");
        }
    }

    public Lobby joinLobbyById(long id, String token) {
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = getLobby(id);

        if (user.getLobby() == null) {
            if (lobby.getLobbyusers().size() < 8) {
                user.setLobby(lobby);

                lobby.addUserToLobby(user);
                return lobby;
            } else {// Throw an exception if the lobby is full
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby is full!");
            }
        } else {
            // Throw an exception if the user is already in a lobby
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in a lobby!");
        }
    }

    public void removeUserFromLobby(String token) {
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = user.getLobby();

        if (lobby != null) {
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
                Set<User> lobbyUsers = lobby.getLobbyusers();
                User newLeader = lobbyUsers.iterator().next();
                lobby.setLobbyLeader(newLeader);
            }
            return;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in a Lobby!");
    }


    //start a new Game in lobby
    public Game startGame(String token, long id) {
        Lobby lobby = getLobby(id);
        authenticateLeader(token, lobby);
        HashMap<String, Integer> players = new HashMap<String, Integer>();

        //throw exception if game already running
        if(lobby.getGame() != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "lobby already has running game!");
        }

        //Iterate over all users, check cash, then add username and money to player hashmap
        lobby.getLobbyusers().forEach((u) -> {
            // if enough cash add user to Map
            if(checkCash(u)){
                players.put(u.getUsername(), u.getMoney());
            }

            // Otherwise remove user, if user is leader throw exception, else continue starting game
            else {
                updateUser(u);

                // If user is lobby leader, exception is thrown and game start is cancelled
                if (lobby.getLobbyLeader()== u){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not start game, lobby leader has insufficient money!");
                }

                //remove user  (might not be necessary, could also just update and continue, might change later)
                removeUserFromLobby(u.getToken());
            }
        });

        //checks player amount
        if(players.size() >= 3){
            return lobby.createGame(players, id);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough players");

    }


    //Updates user Money
    private void updateUser(User user){
        user.setTries(user.getTries() + 1);
        user.setMoney(2000);
    }

    //Check if provided token belongs to lobby leader, throws exception if not, does nothing otherwise.
    private void authenticateLeader(String token, Lobby lobby) {
        userService.authenticateUser(token);

        //compare provided token and leader token
        if (!Objects.equals(lobby.getLobbyLeader().getToken(), token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the lobby leader can start the game!");
        }
    }

    //checks for Cash, return bool
    private boolean checkCash(User user){
        return user.getMoney() >= 2000;
    }

    //returns requested lobby, if lobby doesn't exist throws exception
    private Lobby getLobby(long lobbyId){
        Lobby lobby = lobbyRepository.findById(lobbyId);
        if(lobby == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown lobby");
        }
        return lobby;
    }

}