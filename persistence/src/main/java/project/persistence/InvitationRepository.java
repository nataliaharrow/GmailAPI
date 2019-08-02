package project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Invitation findFirstByPlayerEmail(String email);
}
