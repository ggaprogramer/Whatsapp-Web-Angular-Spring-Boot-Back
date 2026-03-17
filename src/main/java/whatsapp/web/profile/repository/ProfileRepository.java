package whatsapp.web.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsapp.web.profile.model.Profile;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
