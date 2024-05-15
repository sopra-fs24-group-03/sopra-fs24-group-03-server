package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.constant.Moves;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPrivateGetDTO;
import javassist.expr.NewArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Captor
    ArgumentCaptor<List<PlayerHand>> captor;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Game game;
    @Mock
    private Pot pot;

    @Mock
    private GameTable table;

    @Mock
    private Player player;

    @Mock
    private GamePutDTO move;


    @Spy
    @InjectMocks
    private GameService gameService;

    @Test
    void getGameById_Success() {
        long gameId = 1L;
        String validToken = "valid-token";
        Mockito.doNothing().when(userService).authenticateUser(validToken);
        when(gameRepository.findById(gameId)).thenReturn(game);
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        List<Player> players = Arrays.asList(new Player(game, "Player1", 100,  validToken, cards), new Player(game, "Player2", 100,  "other", cards));
        when(game.getPlayers()).thenReturn(players);


        Game retrievedGame = gameService.getGameById(gameId, validToken);
        assertNotNull(retrievedGame);
    }
    @Test
    void getGameById_Fail() {
        long gameId = 1L;
        String invalidToken = "invalid-token";
        when(gameRepository.findById(gameId)).thenReturn(game);
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()->  gameService.getGameById(gameId, invalidToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());

    }
    @Test
    void getPlayerByToken_Success() {
        String validToken = "valid-token";
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        List<Player> players = Arrays.asList(new Player(game, "Player1", 100,  validToken, cards), new Player(game, "Player2", 100,  "other", cards));

        Player retrievedPlayer = gameService.getPlayerByToken(players, validToken);

        assertNotNull(retrievedPlayer);
        assertEquals("valid-token", retrievedPlayer.getToken());
    }
    @Test
    void filterPlayersAllIn_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(true);
        player1.setTotalBettingInCurrentRound(100);
        player2.setAllIn(false);
        player2.setTotalBettingInCurrentRound(100);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        // Call the method to filter players
        List<Player> qualifiedPlayers = gameService.filterPlayersAllIn(players);

        // Asserts: Check that only the correct players are included
        assertNotNull(qualifiedPlayers);
        assertEquals(1, qualifiedPlayers.size());
        assertTrue(qualifiedPlayers.contains(players.get(0)));
    }
    @Test
    void filterPlayersNotFolded_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setFolded(false); // Player1 has not folded
        player2.setFolded(true);  // Player2 has folded

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Call the method to filter players
        List<Player> qualifiedPlayers = gameService.filterPlayersNotFolded(players);

        // Asserts: Check that only the correct players are included
        assertNotNull(qualifiedPlayers);
        assertEquals(1, qualifiedPlayers.size());
        assertTrue(qualifiedPlayers.contains(players.get(0)));
    }
    @Test
    void PlayersAllInBoolean_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(true);
        player2.setAllIn(false);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Call the method
        boolean result = gameService.playersAllIn(players);
        // Asserts: Check that only the correct players are included
        assertTrue(result);
    }
    @Test
    void PlayersAllInOrFoldedBoolean_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(true);
        player2.setFolded(true);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(game.getPlayers()).thenReturn(players);

        // Call the method
        boolean result = gameService.playersFoldedOrAllIn(game);
        // Asserts:
        assertTrue(result);
    }
    @Test
    void setIndextoSBPlayer_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(false);
        player1.setFolded(false);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(game.getSmallBlindPlayer()).thenReturn(player1);
        when(game.getPlayers()).thenReturn(players);

        // Call the method
        gameService.setIndexToSBPlayer(game);
        // Asserts:
        verify(game).setPlayerTurnIndex(0);
    }
    @Test
    void setRaisePlayerCorrectSB_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(false);
        player1.setFolded(false);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(game.getSmallBlindPlayer()).thenReturn(player1);
        when(game.getPlayers()).thenReturn(players);

        // Call the method
        Player raisePlayer = gameService.setRaisePlayerCorrect(game);
        // Asserts:
        assertEquals(player1, raisePlayer);
    }
    @Test
    void setRaisePlayerCorrectOtherPlayer_Success() {
        // Setup:
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};

        Player player1 = new Player(game, "Player1", 100,  "token", cards);
        Player player2 = new Player(game, "Player2", 100,  "other", cards);
        player1.setAllIn(true);
        player1.setFolded(false);
        player2.setAllIn(false);
        player2.setFolded(false);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(game.getSmallBlindPlayer()).thenReturn(player1);
        when(game.getPlayers()).thenReturn(players);

        // Call the method
        Player raisePlayer = gameService.setRaisePlayerCorrect(game);
        // Asserts:
        assertEquals(player2, raisePlayer);
    }



    @Test
    public void turn_foldSuccess(){
        User user = new User();
        //List<User> = new ArrayList<>()
        user.setUsername("username");
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QS", "image"));
        }};

        Game game = mock(Game.class);

        Player player = new Player(game, "hans", 1, "t", cards);
        player.setId(2L);
        List<Player> players = new ArrayList<>();
        players.add(player);

        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(move.getMove()).thenReturn(Moves.Fold);
        when(game.getGameTable()).thenReturn(table);
        when(table.getOpenCards()).thenReturn(cards);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(gameService).startTimer(Mockito.anyLong(), Mockito.anyString());




        int bet = gameService.turn(move, 1, "token");

        assertEquals(0, bet);
    }

    @Test
    public void turn_raiseSuccess(){
        User user = new User();

        user.setUsername("username");
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QS", "image"));
        }};

        Player player1 = new Player(game, "hans", 1, "t", cards);
        player1.setId(2L);
        player1.setLastRaiseAmount(1);
        List<Player> players = new ArrayList<>();
        players.add(player1);



        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(0);
        when(player.getMoney()).thenReturn(2000);
        when(move.getAmount()).thenReturn(100);
        when(move.getMove()).thenReturn(Moves.Raise);
        Mockito.doNothing().when(game).setBet(Mockito.anyInt());
        when(game.getGameTable()).thenReturn(table);
        when(table.getOpenCards()).thenReturn(cards);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(gameService).startTimer(Mockito.anyLong(), Mockito.anyString());

        int bet = gameService.turn(move, 1, "token");
        assertEquals(100, bet);
    }
    @Test
    public void turn_raiseFailNotEnoughMoney(){
        User user = new User();

        user.setUsername("username");



        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(player.getMoney()).thenReturn(50);
        when(move.getAmount()).thenReturn(100);
        when(move.getMove()).thenReturn(Moves.Raise);


        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    @Test
    public void turn_raiseToLittleRaise(){
        User user = new User();

        user.setUsername("username");



        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(100);
        when(move.getAmount()).thenReturn(0);
        when(move.getMove()).thenReturn(Moves.Raise);



        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void turn_checkSuccess(){
        User user = new User();
        user.setUsername("username");
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QS", "image"));
        }};

        Player player1 = new Player(game, "hans", 1, "t", cards);
        player1.setId(2L);
        player1.setLastRaiseAmount(1);
        List<Player> players = new ArrayList<>();
        players.add(player1);

        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(0);
        when(move.getAmount()).thenReturn(0);
        when(move.getMove()).thenReturn(Moves.Check);
        when(game.getGameTable()).thenReturn(table);
        when(table.getOpenCards()).thenReturn(cards);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(gameService).startTimer(Mockito.anyLong(), Mockito.anyString());

        int bet = gameService.turn(move, 1, "token");
        assertEquals(0, bet);
    }
    @Test
    public void turn_checkFail(){
        User user = new User();
        user.setUsername("username");


        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(100);
        when(move.getAmount()).thenReturn(0);
        when(move.getMove()).thenReturn(Moves.Check);


        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void turn_callSuccess(){
        User user = new User();

        user.setUsername("username");
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
            add(new Card("QS", "image"));
        }};

        Player player1 = new Player(game, "hans", 1, "t", cards);
        player1.setId(2L);
        player1.setLastRaiseAmount(1);
        List<Player> players = new ArrayList<>();
        players.add(player1);

        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(100);
        when(player.getMoney()).thenReturn(2000);
        when(move.getAmount()).thenReturn(0);
        when(player.getLastRaiseAmount()).thenReturn(0);
        when(move.getMove()).thenReturn(Moves.Call);
        when(game.getGameTable()).thenReturn(table);
        when(table.getOpenCards()).thenReturn(cards);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(gameService).startTimer(Mockito.anyLong(), Mockito.anyString());

        int bet = gameService.turn(move, 1, "token");
        assertEquals(100, bet);
    }
    @Test
    public void turn_callFail(){
        User user = new User();

        user.setUsername("username");


        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getBet()).thenReturn(0);
        when(move.getMove()).thenReturn(Moves.Call);


        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> gameService.turn(move, 1, "token")
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
    @Test
    public void authorize_Success(){
        User user = new User();
        user.setUsername("username");
        Player player = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(player);

        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getPlayers()).thenReturn(players);
        when(game.getPlayerTurnIndex()).thenReturn(0);

        //throws no error
        gameService.authorize("token", 1);

    }
    @Test
    public void authorize_Unauthorized(){
        User user = new User();
        user.setUsername("username");
        Player player = mock(Player.class);
        Player anotherPlayer = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(anotherPlayer);

        when(userRepository.findByToken(Mockito.anyString())).thenReturn(user);
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(game.getPlayerByUsername(Mockito.anyString())).thenReturn(player);
        when(game.getPlayers()).thenReturn(players);
        when(game.getPlayerTurnIndex()).thenReturn(1);

        //throws  error
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()->  gameService.authorize("token", 1)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());


    }



    @Test
    public void updateGame_UpdatesTableMoney() {
        long gameId = 1L;
        int betAmount = 100;
        Game game = mock(Game.class);
        GameTable table = mock(GameTable.class);

        Player currentPlayer = mock(Player.class);
        List<Player> players = new ArrayList<>();
        players.add(currentPlayer);

        when(gameRepository.findById(gameId)).thenReturn(game);
        when(game.getGameTable()).thenReturn(table);
        when(game.getPlayers()).thenReturn(players);
        when(game.getPlayerTurnIndex()).thenReturn(0);
        when(currentPlayer.getUsername()).thenReturn("username");
        when(gameService.playersFolded(game)).thenReturn(false);
        when(table.getPotByName("mainPot")).thenReturn(pot);


        // Explicitly block winningCondition from being called
        Mockito.doNothing().when(gameService).endGame(Mockito.anyLong());

        gameService.updateGame(gameId, betAmount);

        verify(table.getPotByName("mainPot"), Mockito.times(1)).updateMoney(betAmount); // Check if table.updateMoney was called with the correct amount
    }


    @Test
    public void evaluateHand_flush(){
        List<Card> playerCards = Stream.of(
                new Card("AH", "image"),
                new Card("2H", "image")
        ).collect(Collectors.toList());

        List<Card> tableCards = Stream.of(
                new Card("3S", "image"),
                new Card("4H", "image"),
                new Card("5S", "image"),
                new Card("0H", "image"),
                new Card("AH", "image")
        ).collect(Collectors.toList());

        when(player.getCards()).thenReturn(playerCards);
        when(table.getCards()).thenReturn(tableCards);

        PlayerHand hand = gameService.evaluateHand(player, table);

        assertEquals(Hand.FLUSH, hand.getHand());
        assertEquals(5, hand.getCards().size());
        assertEquals("AH", hand.getCards().get(0).getCode());
        assertEquals("2H", hand.getCards().get(4).getCode());
    }

    @Test
    public void evaluateHand_highCard(){
        List<Card> playerCards = Stream.of(
                new Card("7H", "image"),
                new Card("2H", "image")
        ).collect(Collectors.toList());

        List<Card> tableCards = Stream.of(
                new Card("3S", "image"),
                new Card("4D", "image"),
                new Card("KS", "image"),
                new Card("0H", "image"),
                new Card("AH", "image")
        ).collect(Collectors.toList());

        when(player.getCards()).thenReturn(playerCards);
        when(table.getCards()).thenReturn(tableCards);

        PlayerHand hand = gameService.evaluateHand(player, table);

        assertEquals(Hand.HIGH_CARD, hand.getHand());
        assertEquals(5, hand.getCards().size());
        assertEquals("AH", hand.getCards().get(0).getCode());
        assertEquals("4D", hand.getCards().get(4).getCode());
    }


    @Test
    public void winningCondition(){
        //Setup
        List<Card> cards = new ArrayList<>(){{
            add(new Card("3S", "image"));
            add(new Card("4D", "image"));
            add(new Card("KS", "image"));
            add(new Card("0H", "image"));
            add(new Card("AH", "image"));
        }};

        Player player1 = new Player(game, "username1", 1, "token1", cards);
        Player player2 = new Player(game, "username2", 1, "token2", cards);

        PlayerHand playerHand1 = new PlayerHand();
        PlayerHand playerHand2 = new PlayerHand();
        playerHand1.setHand(Hand.STRAIGHT_FLUSH);
        playerHand2.setHand(Hand.ROYAL_FLUSH);
        playerHand1.setCards(cards);
        playerHand2.setCards(cards);

        List<Player> players = Stream.of(player1, player2).toList();

        when(pot.getGameTable()).thenReturn(table);
        when(pot.getEligiblePlayers()).thenReturn(players);
        Mockito.doReturn(playerHand1).doReturn(playerHand2).when(gameService).evaluateHand(Mockito.any(), Mockito.any());

        //method call
        List<PlayerHand> result = gameService.winningCondition(pot);


        assertEquals(1, result.size());
        assertEquals(playerHand2, result.get(0));
    }

    @Test
    public void winningCondition_draw(){
        //Setup
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KD", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("2H", "image"));
        }};

        Player player1 = new Player(game, "username1", 1, "token1", cards);
        Player player2 = new Player(game, "username2", 1, "token2", cards);

        PlayerHand playerHand1 = new PlayerHand();
        PlayerHand playerHand2 = new PlayerHand();
        playerHand1.setHand(Hand.HIGH_CARD);
        playerHand2.setHand(Hand.HIGH_CARD);
        playerHand1.setCards(cards);
        playerHand2.setCards(cards);

        List<Player> players = Stream.of(player1, player2).toList();

        when(pot.getGameTable()).thenReturn(table);
        when(pot.getEligiblePlayers()).thenReturn(players);
        Mockito.doReturn(playerHand1).doReturn(playerHand2).when(gameService).evaluateHand(Mockito.any(), Mockito.any());

        //method call
        List<PlayerHand> result = gameService.winningCondition(pot);


        assertEquals(2, result.size());
        assert(result.contains(playerHand2));
        assert(result.contains(playerHand1));
    }

    @Test
    public void winningCondition_sameHand(){
        //Setup
        List<Card> cards = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KD", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("3H", "image"));
        }};

        List<Card> cards2 = new ArrayList<>(){{
            add(new Card("AS", "image"));
            add(new Card("KD", "image"));
            add(new Card("0S", "image"));
            add(new Card("9H", "image"));
            add(new Card("2H", "image"));
        }};

        Player player1 = new Player(game, "username1", 1, "token1", cards);
        Player player2 = new Player(game, "username2", 1, "token2", cards);

        PlayerHand playerHand1 = new PlayerHand();
        PlayerHand playerHand2 = new PlayerHand();
        playerHand1.setHand(Hand.HIGH_CARD);
        playerHand2.setHand(Hand.HIGH_CARD);
        playerHand1.setCards(cards);
        playerHand2.setCards(cards2);

        List<Player> players = Stream.of(player1, player2).toList();

        when(pot.getGameTable()).thenReturn(table);
        when(pot.getEligiblePlayers()).thenReturn(players);
        Mockito.doReturn(playerHand1).doReturn(playerHand2).when(gameService).evaluateHand(Mockito.any(), Mockito.any());

        //method call
        List<PlayerHand> result = gameService.winningCondition(pot);

        assertEquals(1, result.size());
        assertEquals(playerHand1, result.get(0));
    }

    @Test
    public void endGame_noRetry(){
        List<Player> players = new ArrayList<>(){{
            add(mock(Player.class));
            add(mock(Player.class));
        }};

        List<PlayerHand> winner = new ArrayList<>(){{
            add(mock(PlayerHand.class));
        }};

        List<Pot> pots = new ArrayList<>(){{
            add(new Pot(1000, "mainPot"));
        }};


        User user = new User();
        User user2 = new User();
        user.setUsername("user1");
        user2.setUsername("user2");
        user.setMoney(2000);
        user2.setMoney(2000);
        user.setTries(0);
        user2.setTries(1);

        //repos
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(user);
        when(userRepository.findByUsername(eq(user2.getUsername()))).thenReturn(user2);

        //game
        when(game.getGameTable()).thenReturn(table);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(game).setWinner(Mockito.any());

        //player
        when(players.get(0).getUsername()).thenReturn("user1");
        when(players.get(1).getUsername()).thenReturn("user2");
        when(players.get(0).getMoney()).thenReturn(1000);
        when(players.get(1).getMoney()).thenReturn(500);

        //table
        when(table.getPots()).thenReturn(pots);

        //PlayerHand
        when(winner.get(0).getPlayer()).thenReturn(players.get(0));

        //gameService
        Mockito.doNothing().when(gameService).deleteGame(Mockito.any(), Mockito.anyInt());
        when(gameService.winningCondition(pots.get(0))).thenReturn(winner);

        //method call
        gameService.endGame(0L);

        //insure user money has been updated
        assertEquals(2000, user.getMoney());
        assertEquals(500, user2.getMoney());
        assertEquals(1, user2.getTries());
        assertEquals(0, user.getTries());

        //insure correct methods were called
        verify(game).setWinner(eq(winner));
        verify(gameService).deleteGame(eq(game), Mockito.anyInt());
    }

    @Test
    public void endGame_multiplePots(){
        List<Player> players = new ArrayList<>(){{
            add(mock(Player.class));
            add(mock(Player.class));
            add(mock(Player.class));
        }};

        List<PlayerHand> winner1 = new ArrayList<>(){{
            add(mock(PlayerHand.class));
        }};

        List<PlayerHand> winner2 = new ArrayList<>(){{
            add(mock(PlayerHand.class));
        }};

        List<Pot> pots = new ArrayList<>(){{
            add(new Pot(1000, "mainPot"));
            add(new Pot(500, "sidepot1"));
        }};

        User user = new User();
        User user2 = new User();
        User user3 = new User();
        user.setUsername("user1");
        user2.setUsername("user2");
        user3.setUsername("user3");
        user.setMoney(2000);
        user2.setMoney(2000);
        user3.setMoney(2000);
        user.setTries(0);
        user2.setTries(1);
        user3.setTries(2);

        //repos
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(user);
        when(userRepository.findByUsername(eq(user2.getUsername()))).thenReturn(user2);
        when(userRepository.findByUsername(eq(user3.getUsername()))).thenReturn(user3);

        //game
        when(game.getGameTable()).thenReturn(table);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(game).setWinner(Mockito.any());

        //player
        when(players.get(0).getUsername()).thenReturn("user1");
        when(players.get(1).getUsername()).thenReturn("user2");
        when(players.get(2).getUsername()).thenReturn("user3");
        when(players.get(0).getMoney()).thenReturn(1000);
        when(players.get(1).getMoney()).thenReturn(50);
        when(players.get(2).getMoney()).thenReturn(1000);

        //table
        when(table.getPots()).thenReturn(pots);

        //PlayerHand
        when(winner1.get(0).getPlayer()).thenReturn(players.get(0));
        when(winner2.get(0).getPlayer()).thenReturn(players.get(1));

        //gameService
        Mockito.doNothing().when(gameService).deleteGame(Mockito.any(), Mockito.anyInt());
        when(gameService.winningCondition(pots.get(0))).thenReturn(winner1);
        when(gameService.winningCondition(pots.get(1))).thenReturn(winner2);

        //method call
        gameService.endGame(0L);

        //insure user money has been updated
        assertEquals(2000, user.getMoney());
        assertEquals(550, user2.getMoney());
        assertEquals(1000, user3.getMoney());
        assertEquals(1, user2.getTries());
        assertEquals(0, user.getTries());
        assertEquals(2, user3.getTries());

        //insure correct methods were called
        verify(game).setWinner(eq(winner2));
        verify(gameService).deleteGame(eq(game), Mockito.anyInt());
    }

    @Test
    public void endGame_userRetry(){
        List<Player> players = new ArrayList<>(){{
            add(mock(Player.class));
            add(mock(Player.class));
        }};

        List<PlayerHand> winner = new ArrayList<>(){{
            add(mock(PlayerHand.class));
        }};

        List<Pot> pots = new ArrayList<>(){{
            add(new Pot(1000, "mainPot"));
        }};

        pot.setEligiblePlayers(players);

        User user = new User();
        User user2 = new User();
        user.setUsername("user1");
        user2.setUsername("user2");
        user.setMoney(2000);
        user2.setMoney(2000);
        user.setTries(0);
        user2.setTries(1);

        //repos
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(user);
        when(userRepository.findByUsername(eq(user2.getUsername()))).thenReturn(user2);

        //game
        when(game.getGameTable()).thenReturn(table);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(game).setWinner(Mockito.any());

        //player
        when(players.get(0).getUsername()).thenReturn("user1");
        when(players.get(1).getUsername()).thenReturn("user2");
        when(players.get(0).getMoney()).thenReturn(1000);
        when(players.get(1).getMoney()).thenReturn(50);

        //table
        when(table.getPots()).thenReturn(pots);

        //PlayerHand
        when(winner.get(0).getPlayer()).thenReturn(players.get(0));

        //gameService
        Mockito.doNothing().when(gameService).deleteGame(Mockito.any(), Mockito.anyInt());
        when(gameService.winningCondition(pots.get(0))).thenReturn(winner);

        //method call
        gameService.endGame(0L);

        //insure user money has been updated
        assertEquals(2000, user.getMoney());
        assertEquals(2000, user2.getMoney());
        assertEquals(2, user2.getTries());
        assertEquals(0, user.getTries());

        //insure correct methods were called
        verify(game).setWinner(eq(winner));
        verify(gameService).deleteGame(eq(game), Mockito.anyInt());
    }

    @Test
    public void endGame_draw(){
        List<Player> players = new ArrayList<>(){{
            add(mock(Player.class));
            add(mock(Player.class));
            add(mock(Player.class));
        }};

        List<PlayerHand> winner = new ArrayList<>(){{
            add(mock(PlayerHand.class));
            add(mock(PlayerHand.class));
        }};

        List<Pot> pots = new ArrayList<>(){{
            add(new Pot(1000, "mainPot"));
        }};

        pot.setEligiblePlayers(players);

        User user = new User();
        User user2 = new User();
        User user3 = new User();
        user.setUsername("user1");
        user2.setUsername("user2");
        user3.setUsername("user3");
        user.setMoney(2000);
        user2.setMoney(2000);
        user3.setMoney(2000);
        user.setTries(0);
        user2.setTries(1);
        user3.setTries(2);

        //repos
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(game);
        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(user);
        when(userRepository.findByUsername(eq(user2.getUsername()))).thenReturn(user2);
        when(userRepository.findByUsername(eq(user3.getUsername()))).thenReturn(user3);

        //game
        when(game.getGameTable()).thenReturn(table);
        when(game.getPlayers()).thenReturn(players);
        Mockito.doNothing().when(game).setWinner(Mockito.any());

        //player
        when(players.get(0).getUsername()).thenReturn("user1");
        when(players.get(1).getUsername()).thenReturn("user2");
        when(players.get(2).getUsername()).thenReturn("user3");
        when(players.get(0).getMoney()).thenReturn(1000);
        when(players.get(1).getMoney()).thenReturn(50);
        when(players.get(2).getMoney()).thenReturn(1000);

        //table
        when(table.getPots()).thenReturn(pots);

        //PlayerHand
        when(winner.get(0).getPlayer()).thenReturn(players.get(0));
        when(winner.get(1).getPlayer()).thenReturn(players.get(1));

        //gameService
        Mockito.doNothing().when(gameService).deleteGame(Mockito.any(), Mockito.anyInt());
        when(gameService.winningCondition(pots.get(0))).thenReturn(winner);



        gameService.endGame(0L);

        //insure user money has been updated
        assertEquals(1500, user.getMoney());
        assertEquals(550, user2.getMoney());
        assertEquals(1000, user3.getMoney());
        assertEquals(1, user2.getTries());
        assertEquals(0, user.getTries());
        assertEquals(2, user3.getTries());

        //insure correct methods were called
        verify(game).setWinner(eq(winner));
        verify(gameService).deleteGame(eq(game), Mockito.anyInt());
    }

    @Test
    public void calculatePotsTwoAllIn(){
        Game game = mock(Game.class);
        Card card = mock(Card.class);
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        GameTable gameTable = new GameTable(cards);


        when(game.getGameTable()).thenReturn(gameTable);

        // Create mock players
        Player player1 = new Player(game,"player1",100,"token1",cards);
        player1.setTotalBettingInCurrentRound(100);
        player1.setAllIn(false);
        Player player2 = new Player(game,"player2",0,"token2",cards);
        player2.setTotalBettingInCurrentRound(50);
        player2.setAllIn(true);
        Player player3 = new Player(game,"player3",0,"token3",cards);
        player3.setTotalBettingInCurrentRound(30);
        player3.setAllIn(true);
        Player player4 = new Player(game,"player4",1000,"token4",cards);
        player4.setTotalBettingInCurrentRound(100);
        player4.setAllIn(false);



        List<Player> allInPlayersOrdered = new ArrayList<>();
        allInPlayersOrdered.add(player3);
        allInPlayersOrdered.add(player2);

        List<Player> allNotFoldedPlayers = new ArrayList<>();
        allNotFoldedPlayers.add(player1);
        allNotFoldedPlayers.add(player2);
        allNotFoldedPlayers.add(player3);
        allNotFoldedPlayers.add(player4);

        List<Pot> pots = new ArrayList<>();
        Pot mainPot = new Pot(280,"mainPot");
        mainPot.setEligiblePlayers(allNotFoldedPlayers);
        pots.add(mainPot);
        gameTable.setPots(pots);

        gameService.calculatePots(game, allInPlayersOrdered);

        assertEquals(3, gameTable.getPots().size());
        assertEquals(100, gameTable.getPots().get(0).getMoney());
        assertEquals(120, gameTable.getPots().get(1).getMoney());
        assertEquals(60, gameTable.getPots().get(2).getMoney());
    }

    @Test
    public void calculatePotsSameAllIn(){
        Game game = mock(Game.class);
        Card card = mock(Card.class);
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        GameTable gameTable = new GameTable(cards);
        when(game.getGameTable()).thenReturn(gameTable);

        // Create mock players
        Player player1 = new Player(game,"player1",0,"token1",cards);
        player1.setTotalBettingInCurrentRound(150);
        player1.setAllIn(true);
        Player player2 = new Player(game,"player2",0,"token2",cards);
        player2.setTotalBettingInCurrentRound(50);
        player2.setAllIn(true);
        Player player3 = new Player(game,"player3",20,"token3",cards);
        player3.setTotalBettingInCurrentRound(200);
        player3.setAllIn(false);
        Player player4 = new Player(game,"player4",1000,"token4",cards);
        player4.setTotalBettingInCurrentRound(200);
        player4.setAllIn(false);
        Player player5 = new Player(game,"player5",0,"token5",cards);
        player5.setTotalBettingInCurrentRound(150);
        player5.setAllIn(true);
        Player player6 = new Player(game,"player6",1000,"token6",cards);
        player6.setTotalBettingInCurrentRound(200);
        player6.setAllIn(false);

        List<Player> allInPlayersOrdered = new ArrayList<>();
        allInPlayersOrdered.add(player2);
        allInPlayersOrdered.add(player1);
        allInPlayersOrdered.add(player5);

        List<Player> allNotFoldedPlayers = new ArrayList<>();
        allNotFoldedPlayers.add(player1);
        allNotFoldedPlayers.add(player2);
        allNotFoldedPlayers.add(player3);
        allNotFoldedPlayers.add(player4);
        allNotFoldedPlayers.add(player5);
        allNotFoldedPlayers.add(player6);

        List<Pot> pots = new ArrayList<>();
        Pot mainPot = new Pot(0,"mainPot");
        mainPot.setEligiblePlayers(allNotFoldedPlayers);
        mainPot.setMoney(950);
        pots.add(mainPot);
        gameTable.setPots(pots);


        gameService.calculatePots(game, allInPlayersOrdered);

        assertEquals(3, gameTable.getPots().size());
        assertEquals(150, gameTable.getPots().get(0).getMoney());
        assertEquals(300, gameTable.getPots().get(1).getMoney());
        assertEquals(500, gameTable.getPots().get(2).getMoney());
    }

    @Test
    public void testGetPlayerByToken_Found() {
        // Setup
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Mockito.lenient().when(player1.getToken()).thenReturn("token123");
        Mockito.lenient().when(player2.getToken()).thenReturn("token456");


        List<Player> players = Arrays.asList(player1, player2);

        // Execution
        Player result = gameService.getPlayerByToken(players, "token123");

        // Verify
        assertEquals(player1, result);
    }

    @Test
    public void testGetPlayerByToken_NotFound() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Mockito.lenient().when(player1.getToken()).thenReturn("token123");
        Mockito.lenient().when(player2.getToken()).thenReturn("token456");

        List<Player> players = Arrays.asList(player1, player2);

        // Execution & Assertion
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> gameService.getPlayerByToken(players, "token789"),
                "Expected getPlayerByToken() to throw, but it didn't"
        );

        // Additional assertions can be done on the exception if needed
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        assertEquals("You are not in this game", thrown.getReason());
    }

    @Test
    public void testSettingPlayerInGameGetDTO() {
        // game
        User user = new User();
        User user2 = new User();
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        Game game = new Game(users);
        game.setPlayerTurnIndex(0);
        List<Player> players = new ArrayList<>();
        //players
        List<Card> cards = new ArrayList<>(){{
            add(new Card("kS", "image"));
            add(new Card("KH", "image"));
        }};
        Player player1 = new Player(game,"player1",0,"token1",cards);
        player1.setId(1L);
        Player player2 = new Player(game,"player2",0,"token2",cards);
        player2.setId(2L);
        players.add(player1);
        players.add(player2);

        PlayerPrivateGetDTO privatePlayer = new PlayerPrivateGetDTO();
        privatePlayer.setId(1L);


        List<PlayerPublicGetDTO> result = gameService.settingPlayerInGameGetDTO(game, players, privatePlayer);

        assertEquals(players.size(), result.size());
        assertEquals("player1", result.get(0).getUsername());
        assertEquals(0, result.get(0).getMoney());
        assertTrue(result.get(0).isTurn());

    }
    @Test
    public void testGameNotFinished() {
        // Setup

        Game game = mock(Game.class);
        when(game.getGameFinished()).thenReturn(false);

        GameGetDTO gameToReturn = new GameGetDTO();
        List<Player> players = new ArrayList<>();

        gameService.addFinishedGamePlayers(gameToReturn, game, players);

        assertNull(gameToReturn.getWinner());
        assertTrue(gameToReturn.getNotFoldedPlayers().isEmpty());
    }


}