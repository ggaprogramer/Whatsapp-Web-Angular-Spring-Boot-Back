package whatsapp.web.authentication.enums;
import lombok.Getter;

@Getter
public enum RolesUsers {
    ADMIN("ADMIN"),
    USER("USER");

    private String descricao;

    RolesUsers(String descricao){
        this.descricao = descricao;
    }
}
