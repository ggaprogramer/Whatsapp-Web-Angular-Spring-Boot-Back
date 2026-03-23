package whatsapp.web.authentication.dto;

import whatsapp.web.authentication.enums.StatusResponse;

public record ResponseLoginDTO (StatusResponse status, String message, String type, String token) {
}
