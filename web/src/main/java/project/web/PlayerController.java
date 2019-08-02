package project.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.domain.Player;
import project.service.PlayerService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping("/players")
    public List<Player> getAllPlayers()
    {
        return playerService.getAllPlayers();
    }

    @PostMapping("/player")
    public Player addNewPlayer(@Valid @RequestBody Player player)
    {
        return playerService.addNewPlayer(player);
    }

//    Create new player attached to Invitation
    @PostMapping("invitation/{id}/player")
    public Player createPlayerWithInvitationId(@PathVariable(value = "id") Long id, @Valid @RequestBody Player player){

        return playerService.createPlayerWithInvitationId(id, player);
    }
}