package whatsapp.web.config.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whatsapp.web.config.EmojiRequestDTO;
import whatsapp.web.config.enums.EmojiType;
import whatsapp.web.config.model.Emoji;
import whatsapp.web.config.service.EmojiService;

import java.util.List;

@RestController
@RequestMapping("/emoji")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @GetMapping("/all")
    public ResponseEntity<List<Emoji>> findAll(){
        return emojiService.findAll();
    }

    @GetMapping("/filter/{type}")
    public ResponseEntity<List<Emoji>> filterByType(@PathVariable EmojiType type){
        return emojiService.filterByType(type);
    }

    /*@PostMapping("/create")
    public ResponseEntity<List<Emoji>> createEmoji(@RequestBody List<EmojiRequestDTO> emojiRequestDTO) {
        return emojiService.createEmoji(emojiRequestDTO);
    }*/
}
