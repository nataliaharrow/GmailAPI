package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.domain.Invitation;
import project.persistence.InvitationRepository;
import java.util.List;

@Service
public class InvitationService {

    protected InvitationService(){}

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public List<Invitation> getAllInvitations(){
        List<Invitation> invitations = invitationRepository.findAll();

        return invitations;
    }

    public Invitation getInvitationByEmail(String email)
    {
        return invitationRepository.findFirstByPlayerEmail(email);
    }

    public Invitation addNewInvitation(Invitation invitation){

        return invitationRepository.save(invitation);
    }
}


