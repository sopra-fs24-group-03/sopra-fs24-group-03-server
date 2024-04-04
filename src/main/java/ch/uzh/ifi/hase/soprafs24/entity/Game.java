package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;
import java.util.HashMap;

@Entity
@Table(name = "GAME")
public class Game {

    public Game(){
        //default constructor
    }


    //Game and Lobby have the same ID, this could be changed, as the lobby is saved within the game already
    public Game(HashMap<String,Integer> players, Lobby lobby, long id) {
        setLobby(lobby);
        setId(id);
        //TODO constructor for Game
    }



    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }
}
