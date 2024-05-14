package ch.uzh.ifi.hase.soprafs24.rest.dto.PotDTO;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO.PlayerPublicGetDTO;

import java.util.ArrayList;
import java.util.List;

public class PotPublicGetDTO {
    public List<PlayerPublicGetDTO> getEligiblePlayers() {
        return eligiblePlayers;
    }

    public void setEligiblePlayers(List<PlayerPublicGetDTO> eligiblePlayers) {
        this.eligiblePlayers = eligiblePlayers;
    }

    private Long id;

    private int money;

    private String name;

    private List<PlayerPublicGetDTO> eligiblePlayers = new ArrayList<>();
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
