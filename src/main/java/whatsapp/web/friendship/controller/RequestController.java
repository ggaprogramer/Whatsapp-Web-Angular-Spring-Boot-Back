package whatsapp.web.friendship.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whatsapp.web.friendship.dto.ProfileFormattedDTO;
import whatsapp.web.friendship.dto.RequestDTO;
import whatsapp.web.friendship.dto.RequestResponseDTO;
import whatsapp.web.friendship.enums.SituationFriendShip;
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

    @DeleteMapping("/friendship")
    public ResponseEntity<RequestResponseDTO> deleteRequestFriendShip(@RequestBody RequestDTO requestDTO,
                                                                      HttpServletRequest request){
        return requestService.deleteRequestFriendShip(requestDTO, request);
    }

    @PostMapping("/friendship-rejected")
    public ResponseEntity<RequestResponseDTO> rejectedRequestFriendShip(@RequestBody RequestDTO requestDTO,
                                                                      HttpServletRequest request){
        return requestService.rejectedRequestFriendShip(requestDTO, request);
    }

    @PostMapping("/friendship-approved")
    public ResponseEntity<RequestResponseDTO> approvedRequestFriendShip(@RequestBody RequestDTO requestDTO,
                                                                        HttpServletRequest request){
        return requestService.approvedRequestFriendShip(requestDTO, request);
    }

    @GetMapping("/friendship/profile-list")
    public ResponseEntity<List<ProfileFormattedDTO>> getProfileListForFriendShip(HttpServletRequest request) {
        return requestService.getProfileListForFriendShip(request);
    }

    @GetMapping("/friendship/profile-list-filter/{status}")
    public ResponseEntity<List<ProfileFormattedDTO>> getProfileListForFriendShipFilterByStatus(
            HttpServletRequest request,
            @PathVariable SituationFriendShip status) {
        return requestService.getProfileListForFriendShipFilterByStatus(request, status);
    }

}
