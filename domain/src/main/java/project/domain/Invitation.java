package project.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "playerEmail")
    private String playerEmail;

    @Column(name = "inviterEmail")
    private String inviterEmail;

    @Column(name = "clubName")
    private String clubName;

    @Column(name = "noResponse")
    private boolean noResponse;

    @OneToMany(mappedBy = "invitation")
    private List<Player> players;

    protected Invitation(){}

    public Invitation(Long id, String firstName, String lastName, String playerEmail, boolean noResponse) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.playerEmail = playerEmail;
        this.noResponse = noResponse;
    }

    public Long getInvitationId() {
        return id;
    }

    public void setInvitationId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public boolean isNoResponse() {
        return noResponse;
    }

    public void setNoResponse(boolean noResponse) {
        this.noResponse = noResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInviterEmail() {
        return inviterEmail;
    }

    public void setInviterEmail(String inviterEmail) {
        this.inviterEmail = inviterEmail;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

}

