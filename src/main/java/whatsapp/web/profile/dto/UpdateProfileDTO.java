package whatsapp.web.profile.dto;

public record UpdateProfileDTO(
        String base64File,
        String mimeType,
        String name,
        String username,
        String description,
        String phone) {
}
