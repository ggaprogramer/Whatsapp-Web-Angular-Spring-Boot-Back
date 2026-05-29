package whatsapp.web.friendship.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Exception;
import whatsapp.web.authentication.enums.RolesUsers;
import whatsapp.web.authentication.enums.StatusResponse;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.service.UserService;
import whatsapp.web.cloudfare.service.CloudfareService;
import whatsapp.web.friendship.dto.ProfileFormattedDTO;
import whatsapp.web.friendship.enums.SituationFriendShip;
import whatsapp.web.friendship.dto.RequestDTO;
import whatsapp.web.friendship.dto.RequestResponseDTO;
import whatsapp.web.friendship.model.FriendShip;
import whatsapp.web.friendship.repository.FriendShipRepository;
import whatsapp.web.profile.model.Profile;
import whatsapp.web.profile.model.ProfilePhoto;
import whatsapp.web.profile.repository.ProfileRepository;
import whatsapp.web.profile.service.ProfileService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    @Value("${spring.cloudfare.bucket-profile-photo-name}")
    private String bucketProfilePhotoName;

    private final FriendShipRepository friendShipRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final CloudfareService cloudfareService;

    @Transactional
    public ResponseEntity<RequestResponseDTO> sendRequestFriendShip(RequestDTO requestDTO, HttpServletRequest request){
        Profile findProfile = profileService.getProfileAuthenticated(request);

        if(findProfile == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Acesso Negado. Por favor, faça o login novamente ou crie uma conta.",
                            "authenticated"));
        }

        String username = requestDTO.username();

        Usuario user = userService.encontrarPorUsername(username);

        if(user == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Usuário não encontrado.",
                            "user_not_found"));
        }

        Profile profile = profileService.findProfileByUser(user);

        if(profile == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "O usuário enviado não possui um perfil associado. Por favor, fale com o suporte.",
                            "user_not_found"));
        }

        FriendShip friendShip1 = friendShipRepository.findByProfileSenderAndProfileRecipient(findProfile, profile);
        FriendShip friendShip2 = friendShipRepository.findByProfileRecipientAndProfileSender(findProfile, profile);
        if((friendShip1 != null && friendShip1.getSituation().equals(SituationFriendShip.PENDING))
                || (friendShip2 != null && friendShip2.getSituation().equals(SituationFriendShip.PENDING))){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Os dois usuários possuem um pedido de amizade pendente entre eles.",
                            "friendship"));
        }
        else if(friendShip1 != null && friendShip1.getSituation().equals(SituationFriendShip.REJECTED)
                && !friendShip1.getTimeBlockRejectOfFriendShip().plusDays(7).isAfter(LocalDateTime.now())){
            long millis1 = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long millis2 = friendShip1.getTimeBlockRejectOfFriendShip()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long days = (millis1 - millis2) / (1000 * 60 * 60 * 24);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Esse usuário rejeitou um pedido de amizade seu nos último(s) " + days +
                                    " dia(s). Por favor, aguarde o período de " +
                                    "bloqueio para enviar um novo pedido de amizade.",
                            "friendship"));
        }
        else if(friendShip2 != null && friendShip2.getSituation().equals(SituationFriendShip.REJECTED)
                && !friendShip2.getTimeBlockRejectOfFriendShip().plusDays(7).isAfter(LocalDateTime.now())){
            long millis1 = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long millis2 = friendShip1.getTimeBlockRejectOfFriendShip()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long days = (millis1 - millis2) / (1000 * 60 * 60 * 24);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Você rejeitou o pedido de amizade desse usuário nos último(s) " + days +
                                    " dia(s). Por favor, aguarde o período de " +
                                    "bloqueio para enviar um novo pedido de amizade.",
                            "friendship"));
        }
        else if((friendShip1 != null && friendShip1.getSituation().equals(SituationFriendShip.APPROVED))
                || (friendShip2 != null && friendShip2.getSituation().equals(SituationFriendShip.APPROVED))){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RequestResponseDTO(
                            StatusResponse.ERROR,
                            "Os dois usuários já possuem uma amizade.",
                            "friendship"));
        }

        FriendShip newFriendShip = new FriendShip();
        newFriendShip.setProfileSender(findProfile);
        newFriendShip.setProfileRecipient(profile);
        newFriendShip.setSituation(SituationFriendShip.PENDING);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new RequestResponseDTO(
                        StatusResponse.SUCCESS,
                        "O pedido de amizade foi solicitado com sucesso. Por favor, aguarde a resposta do " +
                                "usuário.",
                        "friendship"));

    }

    public Boolean verifyFriendShip(Profile profileSender, Profile profileRecipient){
        FriendShip friendShip1 = friendShipRepository
                .findByProfileSenderAndProfileRecipient(profileSender, profileRecipient);

        FriendShip friendShip2 = friendShipRepository
                .findByProfileRecipientAndProfileSender(profileSender, profileRecipient);

        if(friendShip1 != null || friendShip2 != null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public ResponseEntity<List<ProfileFormattedDTO>> getProfileListForFriendShip(HttpServletRequest request) {

        Profile findProfile = profileService.getProfileAuthenticated(request);

        if(findProfile == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }

        List<Profile> profiles = this.profileRepository.findAll()
                .stream()
                .filter(profile -> !profile.getUser().getUsername().equals(findProfile.getUser().getUsername()))
                .filter(profile -> !profile.getUser().getRoles().contains(RolesUsers.ADMIN))
                .filter(profile -> !this.verifyFriendShip(findProfile, profile))
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profiles.stream()
                        .map(profile -> {
                            String linkPhoto = null;
                            if(profile.getPhoto() != null){
                                ProfilePhoto profilePhoto = profile.getPhoto();
                                try{
                                    linkPhoto = cloudfareService.generateLinkFile(this.bucketProfilePhotoName, profilePhoto.getName());
                                } catch (S3Exception | IOException e) {}
                            }
                            return new ProfileFormattedDTO(
                                    profile.getUser().getName(),
                                    profile.getUser().getUsername(),
                                    profile.getUser().getEmail(),
                                    profile.getDescription(),
                                    profile.getPhone(),
                                    linkPhoto
                            );
                        }).collect(Collectors.toList()));

    }
}
