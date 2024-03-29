package project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
