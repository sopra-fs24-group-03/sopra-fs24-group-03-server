package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.constant.Hand;
import ch.uzh.ifi.hase.soprafs24.helpers.Card;
import ch.uzh.ifi.hase.soprafs24.helpers.DeckOfCardsApi;
import ch.uzh.ifi.hase.soprafs24.helpers.PlayerHand;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {


    //Required by Springboot, should not be used otherwise
    protected Game() {
        //default constructor
    }


    public Game(List<User> users) {

        this.playerTurnIndex = 0;
        this.bet = 0;
        this.gameFinished = false;

        DeckOfCardsApi cardsApi = new DeckOfCardsApi(new RestTemplate());
        String deckId = cardsApi.postDeck();
        // create the players and give them cards
        setPlayers(users.stream().map(user -> new Player(this, user.getUsername(), user.getMoney(), user.getToken(), cardsApi.drawCards(deckId, 2))).toList());
        // create Table and give five cards
        this.gameTable = new GameTable(cardsApi.drawCards(deckId, 5));
        this.smallBlindPlayer = players.get(0);
        this.gameTable.getPotByName("mainPot").setEligiblePlayers(this.players);

        setUpBlinds();

    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Player> players = new ArrayList<>();

    @Column(nullable = false)
    private int playerTurnIndex;

    @Column(nullable = false)
    private int bet;

    @Column
    private Hand handName = null;


    @Column
    private Boolean gameFinished;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    private List<Card> handCards = null;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gameTable_id", referencedColumnName = "id")
    private GameTable gameTable;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "raisePlayer_id", referencedColumnName = "id")
    private Player raisePlayer;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "smallBlindPlayer_id", referencedColumnName = "id")
    private Player smallBlindPlayer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    private List<Player> winner = null;

    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    public void setsNextPlayerTurnIndex() {
        int numberOfPlayers = players.size();
        for (int i = 0; i < numberOfPlayers; i++) {
            this.playerTurnIndex = (this.playerTurnIndex + 1) % numberOfPlayers;
            if (!players.get(playerTurnIndex).isFolded() && !players.get(playerTurnIndex).isAllIn()) {
                break;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Transactional
    public Player getPlayerByUsername(String username) {
        for (Player player : players) {
            if (Objects.equals(player.getUsername(), username)) {
                return player;
            }
        }
        //if player folded
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not part of this game!");
    }

    public void setWinner(List<PlayerHand> winningHands) {
        this.handName = winningHands.get(0).getHand();
        this.handCards = winningHands.get(0).getCards();
        for (PlayerHand playerHand : winningHands) {
            this.winner.add(playerHand.getPlayer());
        }
    }


    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public GameTable getGameTable() {
        return gameTable;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerTurnIndex() {
        return playerTurnIndex;
    }

    public void setPlayerTurnIndex(int playerTurnIndex) {
        this.playerTurnIndex = playerTurnIndex;
    }

    public Player getRaisePlayer() {
        return raisePlayer;
    }

    public void setRaisePlayer(Player raisePlayer) {
        this.raisePlayer = raisePlayer;
    }

    public Hand getHandName() {
        return handName;
    }

    public void setHandName(Hand handName) {
        this.handName = handName;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public Boolean getGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(Boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public List<Player> getWinner() {
        return winner;
    }

    public Player getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    public void setSmallBlindPlayer(Player smallBlindPlayer) {
        this.smallBlindPlayer = smallBlindPlayer;
    }
    private void setUpBlinds(){
        // Set blinds
        int smallBlind = 25;
        int bigBlind = 50;

        // Assume players are in order and rotate as per game rounds
        Player smallBlindPlayer = players.get(0);
        Player bigBlindPlayer = players.get(1);

        smallBlindPlayer.setMoney(smallBlindPlayer.getMoney()-smallBlind);
        smallBlindPlayer.setLastRaiseAmount(smallBlind);
        smallBlindPlayer.setTotalBettingInCurrentRound(smallBlind);
        bigBlindPlayer.setMoney(bigBlindPlayer.getMoney()-bigBlind);
        bigBlindPlayer.setLastRaiseAmount(bigBlind);
        bigBlindPlayer.setTotalBettingInCurrentRound(bigBlind);
        this.setBet(bigBlind);


        this.gameTable.getPotByName("mainPot").setMoney(smallBlind+bigBlind);
        this.gameTable.setTotalTableBettingInCurrentRound(smallBlind+bigBlind);

        setsNextPlayerTurnIndex();
        setsNextPlayerTurnIndex();
        //GameService.startTimer(this.id, this.players.get(this.playerTurnIndex).getToken());

    }
}
