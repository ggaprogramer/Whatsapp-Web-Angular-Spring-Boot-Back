package whatsapp.web.friendship.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whatsapp.web.friendship.dto.ProfileFormattedDTO;
import whatsapp.web.friendship.dto.RequestDTO;
import whatsapp.web.friendship.dto.RequestResponseDTO;
import whatsapp.web.friendship.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/friendship")
    public ResponseEntity<RequestResponseDTO> sendRequestFriendShip(@RequestBody RequestDTO requestDTO, HttpServletRequest request){
        return requestService.sendRequestFriendShip(requestDTO, request);
    }

    @GetMapping("/friendship/profile-list")
    public ResponseEntity<List<ProfileFormattedDTO>> getProfileListForFriendShip(HttpServletRequest request) {
        return requestService.getProfileListForFriendShip(request);
    }

}
