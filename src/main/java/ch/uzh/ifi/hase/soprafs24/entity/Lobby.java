package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;



@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;


//    @OneToOne
//    private Game game;
//    @OneToMany
//    private List<User> users;
//
//    @OneToOne
//    private User lobbyLeader;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
