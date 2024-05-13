package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader String token) {
        // fetch all users in the internal representation
        userService.authenticateUser(token);
        List<User> users = userService.getUsers();

        // convert each user to the API representation
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        for (User user : users) {
            UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
            if (user.getLobby() != null) {
                userGetDTO.setLobbyId(user.getLobby().getId());
            }
            else {
                userGetDTO.setLobbyId(null);
            }
            userGetDTOs.add(userGetDTO);
        }

        return userGetDTOs;
    }


    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getByID(@PathVariable long id, @RequestHeader String token) {
        userService.authenticateUser(token);
        User user = userService.getUserById(id);
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        if (user.getLobby() != null) {
            userGetDTO.setLobbyId(user.getLobby().getId());
        }
        else {
            userGetDTO.setLobbyId(null);
        }
        return userGetDTO;
    }


    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserPostResponseDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserPostResponseDTO(createdUser);
    }


    @PutMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPostResponseDTO loginUser(@RequestBody UserPostDTO userPostDTO) {

        //find correct user
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = userService.login(userInput);

        return DTOMapper.INSTANCE.convertEntityToUserPostResponseDTO(user);
    }





    @PutMapping("/users/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logoutUser(@RequestHeader String token) {
        userService.authenticateUser(token);
        userService.logout(token);
    }
}