package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO.LobbyGetDTOComplete;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TableDTO.TablePublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPostResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    //specifically for login/register, also maps the token
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "token", target = "token")
    UserPostResponseDTO convertEntityToUserPostResponseDTO(User user);

    @Mapping(source = "username", target = "username")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "lobbyusers", target = "lobbyusers")
    @Mapping(source = "lobbyLeader", target = "lobbyLeader")
    LobbyGetDTOComplete convertEntityToLobbyGetDTOComplete(Lobby lobby);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "lobbyusers", target = "lobbyUsers")
    @Mapping(source = "lobbyLeader", target = "lobbyLeader")
    @Mapping(source = "game", target = "game")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    default List<UserGetDTO> mapUsersToUsernames(Set<User> users) {
        List<UserGetDTO> userGetDtos = new ArrayList<>();
        for (User user : users) {
            UserGetDTO userToAdd = new UserGetDTO();
            userToAdd.setId(user.getId());
            userToAdd.setMoney(user.getMoney());
            userToAdd.setUsername(user.getUsername());
            userGetDtos.add(userToAdd);

        }
        return userGetDtos;
    }


    // Game Mappings
    @Mapping(source = "id", target = "id")
    @Mapping(source = "gameFinished", target = "gameFinished")
    @Mapping(source = "players", target = "players")
    @Mapping(source = "gameTable", target = "gameTable")
    @Mapping(source = "bet", target = "currentBet")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "folded", target = "folded")
    PlayerPublicGetDTO convertEntityToPlayerPublicDTO(Player player);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "folded", target = "folded")
    @Mapping(source = "cards", target = "cardsImage")
    PlayerPrivateGetDTO convertEntityToPlayerPrivateDTO(Player player);

    @Mapping(source = "openCards", target = "openCardsImage")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "money", target = "money")
    TablePublicGetDTO converEntityToTablePublicGetDTO(GameTable gameTable);

}
