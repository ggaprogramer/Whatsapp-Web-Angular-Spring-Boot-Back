package whatsapp.web.friendship.model;

import jakarta.persistence.*;
import lombok.Data;
import whatsapp.web.friendship.enums.SituationFriendShip;
import whatsapp.web.profile.model.Profile;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "profile_sender_id", referencedColumnName = "id", nullable = false)
    private Profile profileSender;

    @OneToOne
    @JoinColumn(name = "profile_recipient_id", referencedColumnName = "id", nullable = false)
    private Profile profileRecipient;

    @Enumerated(EnumType.STRING)
    private SituationFriendShip situation;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime timeBlockRejectOfFriendShip;

    @PrePersist
    private void prePersist(){
        if(profileSender.getUser().getId().equals(profileRecipient.getUser().getId())
        || profileSender.getId().equals(profileRecipient.getId())){
            throw new RuntimeException("Os dois perfis enviados são iguais. Por favor, envie perfis diferentes para criar uma amizade.");
        }
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        if(this.situation.equals(SituationFriendShip.REJECTED)) {
            this.timeBlockRejectOfFriendShip = LocalDateTime.now();
        }
    }

}
