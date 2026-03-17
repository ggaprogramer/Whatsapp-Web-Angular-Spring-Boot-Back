package whatsapp.web.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.profile.model.Profile;
import whatsapp.web.profile.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

   private final ProfileRepository profileRepository;

   public Profile createProfile(Usuario usuario){
       Profile profile = new Profile();
       profile.setUser(usuario);
       return profileRepository.save(profile);
   }

}
