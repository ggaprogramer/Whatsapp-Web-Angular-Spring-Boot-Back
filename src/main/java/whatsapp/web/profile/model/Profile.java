package whatsapp.web.profile.model;

import jakarta.persistence.*;
import lombok.Data;
import whatsapp.web.authentication.model.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfilePhoto photo;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Usuario user;

    @Column
    private String description;

    @Column
    private String phone;

    @Column(name="date_created", nullable = true)
    private LocalDateTime dateCreated;

    @PrePersist
    private void prePersist(){
        this.dateCreated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

}
