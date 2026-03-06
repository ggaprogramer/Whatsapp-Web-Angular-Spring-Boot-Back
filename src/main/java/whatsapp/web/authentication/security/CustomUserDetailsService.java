package whatsapp.web.authentication.security;

import whatsapp.web.authentication.enumeracoes.RolesUsers;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.repository.UserRepository;
import whatsapp.web.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = userRepository.findByUsername(username);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        Usuario user = usuario.get();

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles()
                        .stream()
                        .map(Enum::name) // Converte cada enum para seu valor String
                        .toArray(String[]::new)) // Transforma em um array de String
                .build();

    }
}
