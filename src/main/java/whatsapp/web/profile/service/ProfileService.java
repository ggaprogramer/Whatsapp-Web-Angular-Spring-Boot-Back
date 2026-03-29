package whatsapp.web.profile.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Exception;
import whatsapp.web.authentication.AuthUtils;
import whatsapp.web.authentication.enums.StatusResponse;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.service.UserService;
import whatsapp.web.cloudfare.service.CloudfareService;
import whatsapp.web.config.dto.ResponseDTO;
import whatsapp.web.profile.dto.ProfileResponseDTO;
import whatsapp.web.profile.dto.UpdateProfileDTO;
import whatsapp.web.profile.enums.UnitSizeFile;
import whatsapp.web.profile.model.Profile;
import whatsapp.web.profile.model.ProfilePhoto;
import whatsapp.web.profile.repository.ProfileRepository;
import whatsapp.web.profile.utils.ProfileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    @Value("${spring.cloudfare.bucket-profile-photo-name}")
    private String bucketProfilePhotoName;

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final ProfileUtils profileUtils;
    private final CloudfareService cloudfareService;
    private final AuthUtils authUtils;

    public Profile createProfile(Usuario usuario) {
        Profile profile = new Profile();
        profile.setUser(usuario);
        return profileRepository.save(profile);
    }

    public Profile getProfileAuthenticated(HttpServletRequest request){
        Optional<Usuario> user = authUtils.getUserAuthenticated(request);
        if (user.isEmpty()) {
            return null;
        }
        Optional<Profile> profile = profileRepository.findByUser(user.get());
        return profile.orElse(null);
    }

    public Profile atualizarPerfil(Profile profile, ProfilePhoto photo,
                                   String name, String username, String description, String phone) {
        if(name != null && !name.isEmpty()){
            userService.updateUser(profile.getUser(), name, "");
        }
        if(username != null && !username.isEmpty()){
            userService.updateUser(profile.getUser(), "", username);
        }
        if(description != null && !description.isEmpty()){
            profile.setDescription(description);
        }
        if(phone != null && !phone.isEmpty()){
            profile.setPhone(phone);
        }
        if(photo != null){
            profile.setPhoto(photo);
        }
        return profileRepository.save(profile);
    }

    public ResponseEntity<ProfileResponseDTO> getProfile(HttpServletRequest request) {
        Profile findProfile = this.getProfileAuthenticated(request);

        if(findProfile == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ProfileResponseDTO(
                            StatusResponse.ERROR,
                            "Acesso Negado. Por favor, faça o login novamente ou crie uma conta.",
                            "authenticated",
                                null, null, null, null,
                            null, null));
        }

        String linkPhoto = null;
        if(findProfile.getPhoto() != null){
            ProfilePhoto profilePhoto = findProfile.getPhoto();
            try{
                linkPhoto = cloudfareService.generateLinkFile(this.bucketProfilePhotoName, profilePhoto.getName());
            } catch (S3Exception | IOException e) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ProfileResponseDTO(
                                StatusResponse.ERROR,
                                "Ocorreu algum erro ao processar a imagem: " + e.getMessage(),
                                "file",
                                null, null, null, null,
                                null, null));
            }
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ProfileResponseDTO(
                    StatusResponse.SUCCESS,
                    "Perfil capturado com sucesso.",
                    "profile",
                    findProfile.getUser().getName(),
                    findProfile.getUser().getUsername(),
                    findProfile.getUser().getEmail(),
                    findProfile.getDescription(),
                    findProfile.getPhone(),
                        linkPhoto)
                );
    }

    public ResponseEntity<ResponseDTO> updateProfile(UpdateProfileDTO updateProfileDTO,
                                                     HttpServletRequest request) {
        String base64File = updateProfileDTO.base64File();
        String mimeType = updateProfileDTO.mimeType();
        String name = updateProfileDTO.name();
        String username = updateProfileDTO.username();
        String description = updateProfileDTO.description();
        String phone = updateProfileDTO.phone();

        Profile findProfile = this.getProfileAuthenticated(request);

        if(findProfile == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(
                            StatusResponse.ERROR,
                            "Acesso Negado. Por favor, faça o login novamente ou crie uma conta.",
                            "authenticated"));
        }

        Usuario findUser = userService.encontrarPorUsername(username);
        if(findUser != null && !findUser.getUsername().equals(username)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(
                            StatusResponse.ERROR,
                            "Esse nome de usuário já existe. Por favor, escolha outro.",
                            "username"));
        }

        ProfilePhoto profilePhoto = null;
        try {
            if (base64File != null || mimeType != null) {
                Integer sizeFileFormated = profileUtils.calculateSizeFileInBytes(base64File);
                Double sizeMax = 5.0;
                UnitSizeFile sizeSymbol = UnitSizeFile.MB;
                Double sizeMaxBytes = profileUtils.convertToBytes(sizeMax, sizeSymbol);

                if (sizeFileFormated >= sizeMaxBytes) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseDTO(
                                    StatusResponse.ERROR,
                                    "O tamanho da foto tem que ser menor do que " + sizeMax + " " + sizeSymbol.getDescription(),
                                    "file"));
                }

                if (findProfile.getPhoto() != null) {
                    Boolean resultDeleteFile = cloudfareService.deleteFile(
                            findProfile.getPhoto().getBucket(),
                            findProfile.getPhoto().getName());
                    if (!resultDeleteFile) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ResponseDTO(
                                        StatusResponse.ERROR,
                                        "Erro ao deletar o arquivo. Por favor, fale com o suporte.",
                                        "file"));
                    }
                }

                String nameFile = findProfile.getUser().getUsername() + "-" + UUID.randomUUID();

                // Decodifica a string base64 em um array de bytes
                byte[] fileBytes = Base64.getDecoder().decode(base64File);
                // Cria um InputStream a partir dos bytes
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

               Boolean resultUploadFile = cloudfareService.uploadFile(
                       this.bucketProfilePhotoName,
                       nameFile,
                       byteArrayInputStream,
                       mimeType);
               if(!resultUploadFile){
                   return ResponseEntity
                           .status(HttpStatus.BAD_REQUEST)
                           .body(new ResponseDTO(
                                   StatusResponse.ERROR,
                                   "Erro ao salvar o arquivo. Por favor, fale com o suporte.",
                                   "file"));
               } else {
                   profilePhoto = new ProfilePhoto();
                   profilePhoto.setBucket(bucketProfilePhotoName);
                   profilePhoto.setName(nameFile);
                   profilePhoto.setProfile(findProfile);
               }
            }
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(
                            StatusResponse.ERROR,
                            "Ocorreu algum erro ao processar a imagem: " + e.getMessage(),
                            "file"));
        }

        this.atualizarPerfil(findProfile, profilePhoto, name, username, description, phone);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(
                        StatusResponse.SUCCESS,
                        "Perfil atualizado com sucesso",
                        "profile"));
    }

}
