package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.domain.Invitation;
import project.domain.Player;
import project.persistence.InvitationRepository;
import project.persistence.PlayerRepository;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    public PlayerService(PlayerRepository playerRepository, InvitationRepository invitationRepository) {
        this.playerRepository = playerRepository;
        this.invitationRepository = invitationRepository;
    }

    public List<Player> getAllPlayers(){
        return playerRepository.findAll();
    }

    public Player addNewPlayer(Player player)
    {
        return playerRepository.save(player);
    }

    public Player createPlayerWithInvitationId(Long id, Player player)
    {
        Invitation findInvitation = invitationRepository.findById(id).get();
        player.setInvitation(findInvitation);

        return playerRepository.save(player);
    }
}
