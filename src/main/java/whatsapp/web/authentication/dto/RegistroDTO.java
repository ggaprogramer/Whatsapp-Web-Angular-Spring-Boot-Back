package whatsapp.web.authentication.dto;
import whatsapp.web.authentication.enumeracoes.RolesUsers;

import java.util.List;

public record RegistroDTO(
        String name,
        String username,
        String password1,
        String password2,
        String email,
        List<RolesUsers> roles) {
}
