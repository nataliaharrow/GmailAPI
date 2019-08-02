package project.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.domain.Invitation;
import project.service.InvitationService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @RequestMapping("/invitations")
    public List<Invitation> getAllInvitations(){
        return invitationService.getAllInvitations();
    }

    @RequestMapping("/invitationbyemail")
    public Invitation getInvitationByEmail(@RequestParam(value = "email") String email){
        return invitationService.getInvitationByEmail(email);
    }

    @PostMapping("/invitation")
    public Invitation addNewInvitation(@Valid @RequestBody Invitation invitation)
    {
        return invitationService.addNewInvitation(invitation);
    }

}





