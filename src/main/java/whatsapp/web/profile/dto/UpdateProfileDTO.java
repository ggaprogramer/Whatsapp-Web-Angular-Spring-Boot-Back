package whatsapp.web.profile.dto;

public record UpdateProfileDTO(
        String base64File,
        String mimeType,
        String name,
        String description,
        String phone) {
}
