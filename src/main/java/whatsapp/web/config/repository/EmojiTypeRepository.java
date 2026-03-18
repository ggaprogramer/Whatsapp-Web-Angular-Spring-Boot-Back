package whatsapp.web.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsapp.web.config.model.Emoji;
import whatsapp.web.config.model.EmojiType;

import java.util.UUID;

public interface EmojiTypeRepository extends JpaRepository<EmojiType, UUID> {

    EmojiType findByValue(String value);

}
