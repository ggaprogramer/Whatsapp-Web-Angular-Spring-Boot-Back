package whatsapp.web.profile.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whatsapp.web.config.dto.ResponseDTO;
import whatsapp.web.friendship.dto.ProfileFormattedDTO;
import whatsapp.web.profile.dto.ProfileResponseDTO;
import whatsapp.web.profile.dto.UpdateProfileDTO;
import whatsapp.web.profile.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO,
                                                     HttpServletRequest request) {
        return profileService.updateProfile(updateProfileDTO, request);
    }

    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getProfile(HttpServletRequest request) {
        return profileService.getProfile(request);
    }

}
