package ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;

public class UserPostDTO {

    private String username;

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
