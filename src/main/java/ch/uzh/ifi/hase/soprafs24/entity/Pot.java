package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POT")
public class Pot implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Column
    private int Money;
    @Column
    private String Name;


    @ManyToMany
    @JoinTable(
            name = "pot_player",
            joinColumns = @JoinColumn(name = "pot_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> eligiblePlayers = new ArrayList<>();

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "gameTable_id", referencedColumnName = "id")
    private GameTable gameTable;

    //Required by Springboot, should not be used otherwise
    protected Pot() {
        //default constructor
    }

    public Pot (int money, String Name){
        this.Money = money;
        this.Name = Name;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void updateMoney(int amount) {
        Money += amount;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = money;
    }

    public List<Player> getEligiblePlayers() {
        return eligiblePlayers;
    }

    public void setEligiblePlayers(List<Player> eligiblePlayers) {
        this.eligiblePlayers = eligiblePlayers;
    }

    public GameTable getGameTable() {
        return gameTable;
    }

    public void setGameTable(GameTable gameTable) {
        this.gameTable = gameTable;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
