package ch.uzh.ifi.hase.soprafs24.service;

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

import java.util.Set;


@Service
@Transactional
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, @Qualifier("userRepository") UserRepository userRepository, @Qualifier("userService") UserService userService) {
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
    public Lobby getLobbyById(long id, String token){
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = lobbyRepository.findById(id);

        if (lobby != null){
            if (user.getLobby() != null && user.getLobby().getId() == id) {
                // Return the lobby if the user is in it
                return lobby;
            } else {
                // Throw an exception if the user is not in the lobby
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users that are in the specified lobby can access its information!");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown Lobby!");
    }

    public Lobby joinLobbyById(long id, String token){
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = lobbyRepository.findById(id);
        if (lobby != null){
            if (user.getLobby() == null) {
                if (lobby.getLobbyusers().size() <= 8 ) {
                    user.setLobby(lobby);

                    lobby.addUserToLobby(user);
                    lobbyRepository.save(lobby);


                    return lobby;
                }
                else {// Throw an exception if the lobby is full
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby is full!");}
            }
            else {
                // Throw an exception if the user is already in a lobby
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in a lobby!");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown Lobby!");
    }
    public void removeUserFromLobbyById(long id, String token){
        userService.authenticateUser(token);
        User user = userRepository.findByToken(token);
        Lobby lobby = lobbyRepository.findById(id);

        if (lobby != null){
            // Check if the user is in the lobby
            if (user.getLobby() != null && user.getLobby().getId().equals(lobby.getId())) {
                // Remove the user from the lobby
                user.setLobby(null);
                lobby.removeUserFromLobby(user);

                // Check if the lobby has no users left
                if (lobby.getLobbyusers().size() == 1) {
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
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not in this lobby!");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown Lobby!");
    }
}
