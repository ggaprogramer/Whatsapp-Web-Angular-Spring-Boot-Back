package whatsapp.web.config.dto;

import whatsapp.web.authentication.enums.StatusResponse;

public record ResponseDTO(StatusResponse status, String message, String type) {
}
