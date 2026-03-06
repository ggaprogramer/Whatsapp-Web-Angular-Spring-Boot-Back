package whatsapp.web.authentication.enumeracoes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum RolesUsers {
    ADMIN("ADMIN"),
    USER("USER");

    private String descricao;

    RolesUsers(String descricao){
        this.descricao = descricao;
    }
}
