package ch.uzh.ifi.hase.soprafs24.hand;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.hand.CheckHand.*;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs24.helpers.Card.getValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CheckHandTest {

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

        PlayerHand result = new Straight().checkHand(cards, player);
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


        PlayerHand result = new Straight().checkHand(cards,player);
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


        PlayerHand result = new Straight().checkHand(cards,player);
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
        PlayerHand result = new Flush().checkHand(cards, player);

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
        PlayerHand result = new Flush().checkHand(cards, player);

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
        PlayerHand result = new StraightFlush().checkHand(cards, player);

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
        PlayerHand result = new StraightFlush().checkHand(cards, player);

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
        PlayerHand result = new StraightFlush().checkHand(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void isRoyalFlush(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AD", "image"));
            add(new Card("AS", "image"));
            add(new Card("KS", "image"));
            add(new Card("QS", "image"));
            add(new Card("JH", "image"));
            add(new Card("JS", "image"));
            add(new Card("0S", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new RoyaleFlush().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.ROYAL_FLUSH, result.getHand());

        assertEquals(14, getValue(result.getCards().get(0)));
        assertEquals(13, getValue(result.getCards().get(1)));
        assertEquals(12, getValue(result.getCards().get(2)));
        assertEquals(11, getValue(result.getCards().get(3)));
        assertEquals(10, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }
    @Test
    public void notRoyalFlush() {

        //create sorted List of cards
        List<Card> cards = new ArrayList<>() {{
            add(new Card("AD", "image"));
            add(new Card("AS", "image"));
            add(new Card("KS", "image"));
            add(new Card("QS", "image"));
            add(new Card("JH", "image"));
            add(new Card("0S", "image"));
            add(new Card("0H", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new RoyaleFlush().checkHand(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void fullHouse_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("KS", "image"));
            add(new Card("0H", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("8D", "image"));
            add(new Card("8H", "image"));
            add(new Card("8S", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new FullHouse().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.FULL_HOUSE, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(8, getValue(result.getCards().get(2)));
        assertEquals(10, getValue(result.getCards().get(3)));
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
        PlayerHand result = new FullHouse().checkHand(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void FourCards_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("4S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("4D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new FourCards().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.FOUR_OF_A_KIND, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(4, getValue(result.getCards().get(0)));
        assertEquals(14, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notFourCards_test(){

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
        PlayerHand result = new FourCards().checkHand(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void ThreeOfAKind_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("4H", "image"));
            add(new Card("4D", "image"));
            add(new Card("4D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new ThreeOfKind().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.THREE_OF_A_KIND, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(4, getValue(result.getCards().get(0)));
        assertEquals(14, getValue(result.getCards().get(3)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notThreeOfAKind_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("4D", "image"));
            add(new Card("4D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new ThreeOfKind().checkHand(cards, player);

        //check result
        assertNull(result);
    }

    @Test
    public void TwoPair_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("0H", "image"));
            add(new Card("4D", "image"));
            add(new Card("4D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new TwoPair().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.TWO_PAIR, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(10, getValue(result.getCards().get(0)));
        assertEquals(4, getValue(result.getCards().get(2)));
        assertEquals(14, getValue(result.getCards().get(4)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notTwoPair_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("4D", "image"));
            add(new Card("4D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new TwoPair().checkHand(cards, player);

        //check result
        assertNull(result);
    }


    @Test
    public void Pair_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("0H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new Pair().checkHand(cards, player);

        //check result
        assertNotNull(result);
        assertEquals(Hand.ONE_PAIR, result.getHand());

        //insure correct order, first comes the three of a kind, then the pair
        assertEquals(10, getValue(result.getCards().get(0)));
        assertEquals(14, getValue(result.getCards().get(2)));
        assertEquals(player, result.getPlayer());
    }

    @Test
    public void notPair_test(){

        //create sorted List of cards
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KH", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("4D", "image"));
            add(new Card("3D", "image"));
            add(new Card("2D", "image"));
        }};

        //Mock a player object
        Player player = mock(Player.class);

        //execute the method
        PlayerHand result = new Pair().checkHand(cards, player);

        //check result
        assertNull(result);
    }
}
