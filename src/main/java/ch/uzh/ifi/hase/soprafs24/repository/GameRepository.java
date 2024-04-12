package ch.uzh.ifi.hase.soprafs24.repository;


import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Lobby, Long> {
    Lobby findById(long id);
}