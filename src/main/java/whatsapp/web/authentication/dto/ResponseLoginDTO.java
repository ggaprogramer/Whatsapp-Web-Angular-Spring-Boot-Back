package whatsapp.web.authentication.dto;

import whatsapp.web.authentication.enumeracoes.StatusResponse;
import org.springframework.http.HttpStatus;

public record ResponseLoginDTO (StatusResponse status, String message, String type, String token) {
}
