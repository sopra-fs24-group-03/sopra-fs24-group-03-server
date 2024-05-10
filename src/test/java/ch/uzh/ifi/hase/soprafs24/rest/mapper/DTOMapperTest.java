package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PotDTO.PotPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO.TablePublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    @Test
    public void testGetLobby_fromLobby_toLobbyGetDTO_success() {
        // create Lobby
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyLeader(user);

        List<User> lobbyUsers = new ArrayList<>();
        lobbyUsers.add(user);

        lobby.setLobbyusers(lobbyUsers);

        // MAP -> Create LobbyGetDTO
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);

        // check content
        assertEquals(lobby.getId(), lobbyGetDTO.getId());
        assertEquals(lobby.getLobbyLeader().getUsername(), lobbyGetDTO.getLobbyLeader().getUsername());
        assertEquals(lobby.getLobbyusers().size(), lobbyGetDTO.getLobbyUsers().size());

    }

    @Test
    public void testGetGame_fromGame_toGameGetDTO_success() {
        // create Game
        User user = new User();
        user.setToken("token");
        user.setUsername("testUsername");
        user.setMoney(2000);

        User user2 = new User();
        user.setToken("token");
        user.setUsername("testUsername");
        user.setMoney(2000);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        Game game = new Game(users);
        game.setId(1L);

        // MAP -> Create GameGetDTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        // check content
        assertEquals(game.getId(), gameGetDTO.getId());
        //1975 because small blind is directly deducted from player upon game creation
        assertEquals(1975, gameGetDTO.getPlayers().get(0).getMoney());
        assertEquals(user.getUsername(), gameGetDTO.getPlayers().get(0).getUsername());

    }

    @Test
    public void testConvertEntityToPotPublicGetDTO_success() {
        Pot pot = new Pot(1, "name");
        pot.setId(123L);
        pot.setMoney(450);
        pot.setName("Holiday Fund");

        PotPublicGetDTO potPublicGetDTO = DTOMapper.INSTANCE.convertEntityToPotPublicGetDTO(pot);

        assertEquals(pot.getId(), potPublicGetDTO.getId());
        assertEquals(pot.getMoney(), potPublicGetDTO.getMoney());
        assertEquals(pot.getName(), potPublicGetDTO.getName());
    }

    @Test
    public void testConvertEntityToTablePublicGetDTO_success() {
        // Create GameTable

        Card card = new Card("KS", "image");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        GameTable gameTable = new GameTable();
        gameTable.setId(101L);
        gameTable.setOpenCards(cards);


        // MAP -> Create TablePublicGetDTO
        TablePublicGetDTO tablePublicGetDTO = DTOMapper.INSTANCE.converEntityToTablePublicGetDTO(gameTable);

        // Check content
        assertEquals(gameTable.getId(), tablePublicGetDTO.getId());
        assertEquals(gameTable.getOpenCards().get(0).getImage(), tablePublicGetDTO.getOpenCardsImage().get(0));

    }

    @Test
    public void testConvertEntityToPlayerPrivateDTO_success() {
        Game game = mock(Game.class);

        Card card = new Card("KS", "image");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        // Create Player
        Player player = new Player(game, "hans", 1, "token", cards);
        player.setId(202L);
        player.setFolded(true);
        //player.setCards("Ace of Spades, King of Hearts");  // Example string for cards
        player.setProfit(150);

        // MAP -> Create PlayerPrivateGetDTO
        PlayerPrivateGetDTO playerPrivateGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerPrivateDTO(player);

        // Check content
        assertEquals(player.getId(), playerPrivateGetDTO.getId());
        assertEquals(player.getMoney(), playerPrivateGetDTO.getMoney());
        assertEquals(player.getUsername(), playerPrivateGetDTO.getUsername());
        assertEquals(player.getFolded(), playerPrivateGetDTO.getFolded());
        assertEquals(player.getCards().get(0).getImage(), playerPrivateGetDTO.getCardsImage().get(0));
        assertEquals(player.getProfit(), playerPrivateGetDTO.getProfit());
    }
}


