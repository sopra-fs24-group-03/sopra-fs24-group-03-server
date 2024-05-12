package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setStatus(UserStatus.ONLINE);
    user.setMoney(2000);

    List<User> allUsers = new ArrayList<>();
    allUsers.add(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    doNothing().when(userService).authenticateUser(Mockito.any());
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", "token");

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].money", is(user.getMoney())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void getSpecificUser_validInput_userIsReturned() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setStatus(UserStatus.ONLINE);
    user.setMoney(2000);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    doNothing().when(userService).authenticateUser(isA(String.class));
    given(userService.getUserById(Mockito.anyLong())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/{userid}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", "token");

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(user.getId().intValue())))
      .andExpect(jsonPath("$.username", is(user.getUsername())))
      .andExpect(jsonPath("$.money", is(user.getMoney())))
      .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
        .andExpect(jsonPath("$.token", is(user.getToken())));
  }


  //Tests user editing put request
  @Test
  public void UpdateUser_validInput_returnsVoid() throws Exception {

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("testUsername");


    doNothing().when(userService).authenticateUser(isA(String.class), isA(long.class));
    doNothing().when(userService).updateUser(isA(User.class), isA(long.class));

      //when
    MockHttpServletRequestBuilder putRequest = put("/users/1")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPutDTO))
      .header("token", "token");

    // then
    mockMvc.perform(putRequest)
      .andExpect(status().isNoContent());
    }

  @Test
  public void UpdateUser_invalidInput_throwsException() throws Exception {
    long userid = 1L;

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("testUsername");

      doNothing().when(userService).authenticateUser(isA(String.class), isA(long.class));
      Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).updateUser(Mockito.any(), Mockito.anyLong());

      //when
      MockHttpServletRequestBuilder putRequest = put("/users/{userid}", userid)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPutDTO))
        .header("token", "token");

        // then
      mockMvc.perform(putRequest)
        .andExpect(status().isNotFound());
    }

  @Test
  public void CreateUser_invalidInput_throwsException() throws Exception {

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");

    given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

      //when
    MockHttpServletRequestBuilder postRequest = post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

      // then
    mockMvc.perform(postRequest)
      .andExpect(status().isConflict());
  }

  @Test
  public void GetUser_invalidInput_throwsException() throws Exception {

    long userid = 1L;

    given(userService.getUserById(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    doNothing().when(userService).authenticateUser(isA(String.class));


    //when
    MockHttpServletRequestBuilder getRequest = get("/users/{userid}", userid)
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", "token");

    // then
    mockMvc.perform(getRequest)
      .andExpect(status().isNotFound());
    }


    /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}