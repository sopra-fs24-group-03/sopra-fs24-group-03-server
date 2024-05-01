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

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;
import static ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PlayerHandTest {

    @Test
    public void isStraight(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QS", "image"));
            add(new Card("JH", "image"));
            add(new Card("0D", "image"));
            add(new Card("9D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        PlayerHand result = straight(cards,player);
        assertNotNull(result);
        assertEquals(5,result.getCards().size());
        assertEquals(result.getHand(), Hand.STRAIGHT);
        assertEquals(13,getValue(result.getCards().get(0)));
        assertEquals(12,getValue(result.getCards().get(1)));
        assertEquals(11,getValue(result.getCards().get(2)));
        assertEquals(10,getValue(result.getCards().get(3)));
        assertEquals(9,getValue(result.getCards().get(4)));
    }

    @Test
    public void specialStraight(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("5S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);


        PlayerHand result = straight(cards,player);
        assertNotNull(result);
        assertEquals(result.getCards().size(),5);
        assertEquals(Hand.STRAIGHT,result.getHand());
        assertEquals(5,getValue(result.getCards().get(0)));
        assertEquals(4,getValue(result.getCards().get(1)));
        assertEquals(3,getValue(result.getCards().get(2)));
        assertEquals(2,getValue(result.getCards().get(3)));
        assertEquals(14,getValue(result.getCards().get(4)));
    }

    @Test
    public void notStraight(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);


        PlayerHand result = straight(cards,player);
        assertNull(result);
    }

    @Test
    public void isFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KS", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3S", "image"));
            add(new Card("2S", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = flush(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.FLUSH, result.getHand());

        assertEquals(14, getValue(result.getCards().get(0)));
        assertEquals(13, getValue(result.getCards().get(1)));
        assertEquals(4, getValue(result.getCards().get(2)));
        assertEquals(3, getValue(result.getCards().get(3)));
        assertEquals(2, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void noFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AH", "image"));
            add(new Card("KS", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3S", "image"));
            add(new Card("2S", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = flush(cards, player);

        //check result
        assertNull(result);

    }

    @Test
    public void isStraightFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QH", "image"));
            add(new Card("JD", "image"));
            add(new Card("JH", "image"));
            add(new Card("0H", "image"));
            add(new Card("9H", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = straightFlush(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.STRAIGHT_FLUSH, result.getHand());

        assertEquals(13, getValue(result.getCards().get(0)));
        assertEquals(12, getValue(result.getCards().get(1)));
        assertEquals(11, getValue(result.getCards().get(2)));
        assertEquals(10, getValue(result.getCards().get(3)));
        assertEquals(9, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void specialStraightFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AD", "image"));
            add(new Card("KH", "image"));
            add(new Card("5D", "image"));
            add(new Card("4D", "image"));
            add(new Card("4H", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = straightFlush(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.STRAIGHT_FLUSH, result.getHand());

        assertEquals(5, getValue(result.getCards().get(0)));
        assertEquals(4, getValue(result.getCards().get(1)));
        assertEquals(3, getValue(result.getCards().get(2)));
        assertEquals(2, getValue(result.getCards().get(3)));
        assertEquals(14, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notStraightFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KS", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3S", "image"));
            add(new Card("2S", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = straightFlush(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void fullHouse_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("KS", "image"));
            add(new Card("KH", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = fullHouse(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.FULL_HOUSE, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(4, getValue(result.getCards().get(2)));
        assertEquals(13, getValue(result.getCards().get(3)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notFullHouse_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = fullHouse(cards, player);

        //check result
        assertNull(result);
    }



}
