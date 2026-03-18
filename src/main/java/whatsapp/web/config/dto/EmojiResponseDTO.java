package whatsapp.web.config.dto;

import lombok.Data;
import whatsapp.web.config.model.Emoji;

import java.util.List;

@Data
public class EmojiResponseDTO {

    String nameType;
    String valueType;
    List<Emoji> emojis;
}
