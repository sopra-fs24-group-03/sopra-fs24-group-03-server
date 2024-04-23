package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("testPassword");
    testUser.setUsername("testUsername");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }


  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void loginUser_validInput_success(){
      testUser.setStatus(UserStatus.OFFLINE);
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      User login = userService.login(testUser);

      assertEquals(testUser.getId(), login.getId());
      assertEquals(testUser.getPassword(), login.getPassword());
      assertEquals(testUser.getUsername(), login.getUsername());
      assertEquals(UserStatus.ONLINE, testUser.getStatus());

  }

  @Test
  public void loginUser_invalidInput_throwsException(){
      testUser.setStatus(UserStatus.OFFLINE);
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      User input = new User();
      input.setUsername("testUsername");
      input.setPassword("falsePassword");

      assertThrows(ResponseStatusException.class, () -> userService.login(input));
  }

  @Test
  public void updateUser_validInput_success(){
      Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(testUser);

      User input = new User();
      input.setUsername("newUsername");

      userService.updateUser(input, 1);

      assertEquals(testUser.getUsername(), input.getUsername());
  }

  @Test
  public void updateUser_invalidInput_throwsException(){
      User existingUser = new User();

      Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(testUser);
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(existingUser);


      User input = new User();
      input.setUsername("newUsername");

      assertThrows(ResponseStatusException.class, () -> userService.updateUser(input, 1));
  }


}
