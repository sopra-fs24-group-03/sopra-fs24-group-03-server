package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;

@Entity
@Table(name = "GAME")
public class Game {
    @Id
    @GeneratedValue
    private Long id;

}
