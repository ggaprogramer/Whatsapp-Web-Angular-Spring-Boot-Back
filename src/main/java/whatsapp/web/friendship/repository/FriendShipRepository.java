package whatsapp.web.friendship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsapp.web.friendship.model.FriendShip;
import whatsapp.web.profile.model.Profile;

import java.util.List;
import java.util.UUID;

public interface FriendShipRepository extends JpaRepository<FriendShip, UUID> {

    FriendShip findByProfileSenderAndProfileRecipient(Profile profileSender, Profile profileRecipient);

    FriendShip findByProfileRecipientAndProfileSender(Profile profileRecipient, Profile profileSender);

    List<FriendShip> findByProfileSenderOrProfileRecipient(Profile profileSender, Profile profileRecipient);
    
}
