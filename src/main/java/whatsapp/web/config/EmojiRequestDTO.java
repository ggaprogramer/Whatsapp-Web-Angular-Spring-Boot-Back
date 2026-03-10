package whatsapp.web.config;

import whatsapp.web.config.enums.EmojiType;

public record EmojiRequestDTO (String name, EmojiType type) {
}
