package whatsapp.web.authentication.model;

import jakarta.persistence.*;
import whatsapp.web.authentication.enumeracoes.RolesUsers;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import whatsapp.web.profile.model.Profile;
import whatsapp.web.profile.model.ProfilePhoto;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=300)
    private String name;

    @Column(nullable = false, length=20)
    private String username;

    @Column(nullable = false, length=300)
    private String password;

    @Column(nullable = false, length=150)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @Column(name="last_login", nullable = true)
    private LocalDateTime lastLogin;

    @Column(name="confirmacao_email")
    private Boolean confirmacaoEmail;

    @PrePersist
    private void verifyConfirmacaoEmail(){
        if(this.confirmacaoEmail == null){
            setConfirmacaoEmail(false);
        }
    }

    //@Convert(converter = RolesUsersConverter.class)
    @JdbcTypeCode(Types.ARRAY) // Define o tipo JDBC como ARRAY
    @Column(name = "roles", columnDefinition = "varchar[]")
    @Enumerated(EnumType.STRING)
    private List<RolesUsers> roles;

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

}
