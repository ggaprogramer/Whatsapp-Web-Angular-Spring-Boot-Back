package whatsapp.web.authentication.service;

import whatsapp.web.authentication.dto.LoginDTO;
import whatsapp.web.authentication.dto.RegistroDTO;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

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

    public Usuario salvarUsuario(RegistroDTO registroDTO){
        Usuario usuario = new Usuario();

        String passwordCriptografada = encoder.encode(registroDTO.password1());
        usuario.setPassword(passwordCriptografada);
        usuario.setEmail(registroDTO.email());
        usuario.setName(registroDTO.name());
        usuario.setUsername(registroDTO.username());
        usuario.setRoles(registroDTO.roles());

        return userRepository.save(usuario);
    }

}
