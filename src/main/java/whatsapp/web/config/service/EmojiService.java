package whatsapp.web.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import whatsapp.web.config.dto.EmojiRequestDTO;
import whatsapp.web.config.dto.EmojiRequestTypeDTO;
import whatsapp.web.config.dto.EmojiResponseDTO;
import whatsapp.web.config.model.EmojiType;
import whatsapp.web.config.model.Emoji;
import whatsapp.web.config.repository.EmojiRepository;
import whatsapp.web.config.repository.EmojiTypeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;
    private final EmojiTypeRepository emojiTypeRepository;

    public ResponseEntity<List<EmojiResponseDTO>> findAll(){
        List<Emoji> emojis = emojiRepository.findAll();
        List<EmojiType> emojiTypes = emojiTypeRepository.findAll();
        List<EmojiResponseDTO> emojiResponseDTOS = new ArrayList<>();

        emojiTypes.stream().forEach(emojiType -> {
            EmojiResponseDTO emojiResponseDTO = new EmojiResponseDTO();
            emojiResponseDTO.setValueType(emojiType.getValue());
            emojiResponseDTO.setNameType(emojiType.getName());
            emojiResponseDTO.setEmojis(new ArrayList<>());
            emojis.stream().forEach(emoji -> {
                if(emoji.getType().equals(emojiType)){
                    emojiResponseDTO.getEmojis().add(emoji);
                }
            });
            emojiResponseDTOS.add(emojiResponseDTO);
        });

        return ResponseEntity.ok(emojiResponseDTOS);
    }

    public ResponseEntity<List<Emoji>> createEmoji(List<EmojiRequestDTO> emojiRequestDTO){
        List<Emoji> emojis = emojiRequestDTO.stream().map(dto -> {
            Emoji emoji = new Emoji();
            emoji.setName(dto.name());
            emoji.setValue(dto.value());
            EmojiType emojiType = emojiTypeRepository.findByValue(dto.typeValue());
            emoji.setType(emojiType);
            return emoji;
        }).toList();

        List<Emoji> savedEmojis = emojiRepository.saveAll(emojis);
        return ResponseEntity.ok(savedEmojis);
    }

    public ResponseEntity<List<EmojiType>> createTypeEmoji(List<EmojiRequestTypeDTO> emojiRequestTypeDTO){
        List<EmojiType> emojiTypes = emojiRequestTypeDTO.stream().map(dto -> {
            EmojiType emojiType = new EmojiType();
            emojiType.setName(dto.name());
            emojiType.setValue(dto.value());
            return emojiType;
        }).toList();

        List<EmojiType> savedEmojiTypes = emojiTypeRepository.saveAll(emojiTypes);
        return ResponseEntity.ok(savedEmojiTypes);
    }

}


