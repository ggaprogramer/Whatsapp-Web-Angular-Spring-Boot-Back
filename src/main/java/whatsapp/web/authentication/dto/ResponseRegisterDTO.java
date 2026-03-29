package whatsapp.web.authentication.dto;

import whatsapp.web.authentication.enums.StatusResponse;

public record ResponseRegisterDTO(StatusResponse status, String type, String message) {
}