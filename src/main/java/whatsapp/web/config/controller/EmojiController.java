package whatsapp.web.config.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whatsapp.web.config.dto.EmojiRequestDTO;
import whatsapp.web.config.dto.EmojiRequestTypeDTO;
import whatsapp.web.config.dto.EmojiResponseDTO;
import whatsapp.web.config.model.EmojiType;
import whatsapp.web.config.model.Emoji;
import whatsapp.web.config.service.EmojiService;

import java.util.List;

@RestController
@RequestMapping("/emoji")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @GetMapping("/all")
    public ResponseEntity<List<EmojiResponseDTO>> findAll(){
        return emojiService.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<List<Emoji>> createEmoji(@RequestBody List<EmojiRequestDTO> emojiRequestDTO) {
        return emojiService.createEmoji(emojiRequestDTO);
    }

    @PostMapping("/create/type")
    public ResponseEntity<List<EmojiType>> createTypeEmoji(@RequestBody List<EmojiRequestTypeDTO> emojiRequestTypeDTO) {
        return emojiService.createTypeEmoji(emojiRequestTypeDTO);
    }
}
