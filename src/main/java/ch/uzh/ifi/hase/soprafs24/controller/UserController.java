package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;
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
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }


    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getByID(@PathVariable long id, @RequestHeader String token) {
        userService.authenticateUser(token);
        User user = userService.getUserById(id);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
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


    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@RequestBody UserPutDTO userPutDTO, @PathVariable long id, @RequestHeader String token) {
        userService.authenticateUser(token, id);
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        userService.updateUser(userInput, id);
    }


    @PutMapping("/users/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logoutUser(@RequestHeader String token) {
        userService.authenticateUser(token);
        userService.logout(token);
    }
}