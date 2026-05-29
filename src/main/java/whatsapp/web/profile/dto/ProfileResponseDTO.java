package whatsapp.web.profile.dto;

import whatsapp.web.authentication.enums.StatusResponse;

public record ProfileResponseDTO(StatusResponse status,
                                 String message,
                                 String type,
                                 String name,
                                 String username,
                                 String email,
                                 Boolean confirmedEmail,
                                 String description,
                                 String phone,
                                 String linkPhoto
                                 ) {

}
