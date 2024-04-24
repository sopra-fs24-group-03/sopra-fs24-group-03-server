package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPassword");
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPassword");
    testUser.setUsername("testUsername");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    testUser2.setPassword("testPassword");
    testUser2.setUsername("testUsername");

    // check that an error is thrown
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        userService.createUser(testUser2);
    });

    //check correct status code is returned
    assertEquals(exception.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  public void getNonexistentUser_throwsException() {
    //User doesn't exist
    assertNull(userRepository.findById(1));

    // check that an error is thrown
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        userService.getUserById(1);
    });

    //check correct status code is returned
    assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
  }

  @Test
  public void authenticate_existingUser(){
      //insure user not already in repository
      assertNull(userRepository.findByToken("1"));

      User user = new User();
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      //add user to repository
      userRepository.save(user);
      userRepository.flush();

      //insure user has been added to repository successfully.
      assert(userRepository.findByToken("1") != null);

      try{
          userService.authenticateUser("1");
      }
      catch (ResponseStatusException e) {
          assert(false);
      }
  }

  @Test
  public void authenticate_nonexistentUser_throwsException(){
      assertNull(userRepository.findByToken("1"));

      // check that an error is thrown
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
          userService.authenticateUser("1");
      });

      //check correct status code is returned
      assertEquals(exception.getStatus(), HttpStatus.UNAUTHORIZED);
  }

  @Test
  public void authenticate_user_correctID(){
      //insure user not already in repository
      assertNull(userRepository.findByToken("1"));

      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      //add user to repository
      userRepository.save(user);
      userRepository.flush();

      assert(Objects.equals(userRepository.findById(1L).getToken(), user.getToken()));

      try{
          userService.authenticateUser("1", 1L);
      }
      catch (ResponseStatusException e){
          assert(false);
      }
  }

  @Test
  public void authenticate_user_incorrectID(){
      //insure user not already in repository
      assertNull(userRepository.findByToken("1"));

      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);


      //add user to repository
      userRepository.save(user);
      userRepository.flush();

      assert(userRepository.findById(2L) == null);

      // check that an error is thrown
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
          userService.authenticateUser("1", 2L);
      });

      //check correct status code is returned
      assertEquals(exception.getStatus(), HttpStatus.FORBIDDEN);
  }
}

