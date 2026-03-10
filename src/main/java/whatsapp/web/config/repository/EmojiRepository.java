package whatsapp.web.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsapp.web.config.model.Emoji;

import java.util.UUID;

public interface EmojiRepository extends JpaRepository<Emoji, UUID> {



}
