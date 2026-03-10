package whatsapp.web.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import whatsapp.web.config.EmojiRequestDTO;
import whatsapp.web.config.enums.EmojiType;
import whatsapp.web.config.model.Emoji;
import whatsapp.web.config.repository.EmojiRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;

    public ResponseEntity<List<Emoji>> findAll(){
        List<Emoji> emojis = emojiRepository.findAll();
        return ResponseEntity.ok(emojis);
    }

    public ResponseEntity<List<Emoji>> filterByType(EmojiType type){
        return ResponseEntity.ok(emojiRepository.findAll()
                .stream()
                .filter(emoji -> emoji.getType().equals(type))
                .toList());
    }

    /*public ResponseEntity<List<Emoji>> createEmoji(List<EmojiRequestDTO> emojiRequestDTO){
        List<Emoji> emojis = emojiRequestDTO.stream().map(dto -> {
            Emoji emoji = new Emoji();
            emoji.setName(dto.name());
            emoji.setType(dto.type());
            return emoji;
        }).toList();

        List<Emoji> savedEmojis = emojiRepository.saveAll(emojis);
        return ResponseEntity.ok(savedEmojis);
    }*/

}


