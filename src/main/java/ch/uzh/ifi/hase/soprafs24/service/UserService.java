package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown User!");
    }

    public User getUserByToken(String token) {
        User user = userRepository.findByToken(token);
        if (user != null) {
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown User!");
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        setOnline(newUser);
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }


    //does the login for the given user input
    public User login(User userInput) {
        User user = userRepository.findByUsername(userInput.getUsername());

        //check if user exists and returns them
        if (user != null && userInput.getPassword().equals(user.getPassword())) {
            setOnline(user);
            return user;
        }
        //Throw Error if user doesn't exist
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password or Username incorrect!");
    }

    //Update user profile
    public void updateUser(User userInput, long id) {
        User user = userRepository.findById(id);
        //if new username is given make sure it's not already in use, if same username is given nothing changes
        if (userInput.getUsername() != null && user != userRepository.findByUsername(userInput.getUsername())) {
            if (userRepository.findByUsername(userInput.getUsername()) == null) {
                user.setUsername(userInput.getUsername());
            }
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "the provided Username is already in use!");
        }
    }

    public void logout(String token) {
        setOffline(userRepository.findByToken(token));
    }

    //Only check token
    public void authenticateUser(String token) {
        User user = userRepository.findByToken(token);

        if (user == null) {
            //UNAUTHORIZED, user is not logged so authorization not possible
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can access this when logged in!");
        }
    }

    //token and user must match
    public void authenticateUser(String token, long id) {
        User user = userRepository.findByToken(token);

        if (user == null) {
            //UNAUTHORIZED, user is not logged so authorization not possible
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can access this when logged in!");
        }
        if (user.getId() != id) {
            //FORBIDDEN, User trying to do unallowed action
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access this when logged in as correct user!");
        }
    }

    //set user Status online
    private void setOnline(User user) {
        user.setStatus(UserStatus.ONLINE);
    }

    //set user Status offline
    private void setOffline(User user) {
        user.setStatus(UserStatus.OFFLINE);
    }
}