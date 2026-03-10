package whatsapp.web.config.model;

import jakarta.persistence.*;
import lombok.Data;
import whatsapp.web.config.enums.EmojiType;

import java.util.UUID;

@Entity
@Table
@Data
public class Emoji {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=1000)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmojiType type;
}
