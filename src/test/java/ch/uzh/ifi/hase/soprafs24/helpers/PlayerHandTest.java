package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand.straight;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerHandTest {

    @Test
    public void isStraight(){

        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("testUsername1");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);



        Card card1 = new Card("A", "2");
        Card card2 = new Card("K", "4");
        Card card3 = new Card("Q", "1");
        Card card4 = new Card("J", "4");
        Card card5 = new Card("0", "3");
        Card card6 = new Card("6", "2");
        Card card7 = new Card("5", "1");
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        cards.add(card6);
        cards.add(card7);

        Game game = new Game(users);
        game.setId(1L);
        game.setPlayerTurnIndex(0);

        List<Card> playerCards = new ArrayList<>();
        playerCards.add(card6);
        playerCards.add(card7);
        Player player1 = new Player(game, "PlayerOne", 2000, "validToken1", playerCards);
        player1.setId(2L);

        Player player2 = new Player(game, "PlayerTwo", 2000, "validToken2", playerCards);
        player1.setId(3L);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game.setPlayers(players);

        PlayerHand result = straight(cards,player1);
        assertNotNull(result);
        assertEquals(result.getHand(), Hand.STRAIGHT);
    }

    @Test
    public void specialStraight(){

        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("testUsername1");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);



        Card card1 = new Card("A", "2");
        Card card2 = new Card("K", "4");
        Card card3 = new Card("5", "1");
        Card card4 = new Card("4", "4");
        Card card5 = new Card("3", "3");
        Card card6 = new Card("2", "2");
        Card card7 = new Card("2", "1");
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        cards.add(card6);
        cards.add(card7);

        Game game = new Game(users);
        game.setId(1L);
        game.setPlayerTurnIndex(0);

        List<Card> playerCards = new ArrayList<>();
        playerCards.add(card6);
        playerCards.add(card7);
        Player player1 = new Player(game, "PlayerOne", 2000, "validToken1", playerCards);
        player1.setId(2L);

        Player player2 = new Player(game, "PlayerTwo", 2000, "validToken2", playerCards);
        player1.setId(3L);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game.setPlayers(players);

        PlayerHand result = straight(cards,player1);
        assertNotNull(result);
        assertEquals(result.getHand(), Hand.STRAIGHT);
    }

    @Test
    public void notStraight(){

        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("testUsername1");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);



        Card card1 = new Card("A", "2");
        Card card2 = new Card("K", "4");
        Card card3 = new Card("Q", "1");
        Card card4 = new Card("J", "4");
        Card card5 = new Card("9", "3");
        Card card6 = new Card("6", "2");
        Card card7 = new Card("5", "1");
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        cards.add(card6);
        cards.add(card7);

        Game game = new Game(users);
        game.setId(1L);
        game.setPlayerTurnIndex(0);

        List<Card> playerCards = new ArrayList<>();
        playerCards.add(card6);
        playerCards.add(card7);
        Player player1 = new Player(game, "PlayerOne", 2000, "validToken1", playerCards);
        player1.setId(2L);

        Player player2 = new Player(game, "PlayerTwo", 2000, "validToken2", playerCards);
        player1.setId(3L);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game.setPlayers(players);

        PlayerHand result = straight(cards,player1);
        assertNull(result);
    }

}
