package whatsapp.web.config.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table
@Data
public class Emoji {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=1000)
    private String name;

    @Column(nullable = false, length=100)
    private String value;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private EmojiType type;

    @Override
    public String toString() {
        return this.value;
    }
}
