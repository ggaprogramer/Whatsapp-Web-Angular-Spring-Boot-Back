package whatsapp.web.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import whatsapp.web.authentication.dto.LoginDTO;
import whatsapp.web.authentication.dto.RegistroDTO;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whatsapp.web.authentication.security.TokenService;
import whatsapp.web.config.service.CookieService;
import whatsapp.web.profile.model.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final CookieService cookieService;
    private final TokenService tokenService;

    public Usuario encontrarPorId(UUID uuid){
        Optional<Usuario> usuario = userRepository.findById(uuid);
        return usuario.orElse(null);
    }
    public Usuario encontrarPorId(String id){
        UUID uuidUser = UUID.fromString(id);
        Optional<Usuario> usuario = userRepository.findById(uuidUser);
        return usuario.orElse(null);
    }

    public List<Usuario> encontrarTodos(){
        return userRepository.findAll();
    }

    public Usuario encontrarPorEmail(String email){
        Optional<Usuario> usuario = userRepository.findByEmail(email);
        return usuario.orElse(null);
    }

    public Usuario encontrarPorUsername(String username){
        Optional<Usuario> usuario = userRepository.findByUsername(username);
        return usuario.orElse(null);
    }

    public Usuario createUser(RegistroDTO registroDTO){
        Usuario usuario = new Usuario();

        String passwordCriptografada = encoder.encode(registroDTO.password1());
        usuario.setPassword(passwordCriptografada);
        usuario.setEmail(registroDTO.email());
        usuario.setName(registroDTO.name());
        usuario.setUsername(registroDTO.username());
        usuario.setRoles(registroDTO.roles());

        return userRepository.save(usuario);
    }

    public void updateLastLogin(Usuario usuario){
        usuario.setLastLogin(LocalDateTime.now());
        userRepository.save(usuario);
    }

    public Usuario updateUser(Usuario user, String name, String username){
        if(name != null && !name.isEmpty()){
            user.setName(name);
            userRepository.save(user);
        }
        else if(username != null && !username.isEmpty()){
            user.setUsername(username);
            userRepository.save(user);
        }
        return user;
    }
}
