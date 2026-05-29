package whatsapp.web.authentication.enums;
import lombok.Getter;

@Getter
public enum RolesUsers {
    ADMIN("ADMIN"),
    USER("USER");

    private String description;

    RolesUsers(String description){
        this.description = description;
    }
}
