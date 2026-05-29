package whatsapp.web.friendship.dto;

import whatsapp.web.authentication.enums.StatusResponse;

public record RequestResponseDTO (StatusResponse status,
                                 String message,
                                 String type
) {

}
