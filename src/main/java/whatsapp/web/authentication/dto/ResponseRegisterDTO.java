package whatsapp.web.authentication.dto;

import whatsapp.web.authentication.enumeracoes.StatusResponse;

public record ResponseRegisterDTO(StatusResponse status, String type, String message) {
}