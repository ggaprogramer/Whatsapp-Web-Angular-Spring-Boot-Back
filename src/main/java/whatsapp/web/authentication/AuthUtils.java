package whatsapp.web.authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.repository.UserRepository;
import whatsapp.web.authentication.security.TokenService;
import whatsapp.web.config.service.CookieService;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final CookieService cookieService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public String getIdUserAuthenticated(HttpServletRequest request) {
        String token = this.cookieService.getCookie("token", request);
        if(token != null){
            String idUser = tokenService.validateToken(token);
            if(idUser != null) return idUser;
            return null;
        };
        return null;
    }

    public Optional<Usuario> getUserAuthenticated(HttpServletRequest request) {
        String token = this.cookieService.getCookie("token", request);
        if(token != null){
            String idUser = tokenService.validateToken(token);
            if(idUser != null) return userRepository.findById(UUID.fromString(idUser));
            return null;
        };
        return null;
    }

}
