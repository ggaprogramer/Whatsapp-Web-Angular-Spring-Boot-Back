package whatsapp.web.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.profile.model.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUser(Usuario user);

}
