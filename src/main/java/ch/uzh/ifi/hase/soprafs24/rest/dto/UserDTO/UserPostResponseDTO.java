package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserPostResponseDTO {
    private Long id;
    private String username;
    private UserStatus status;

    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}