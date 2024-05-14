package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;

    private int tries;
    private int money;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

}

