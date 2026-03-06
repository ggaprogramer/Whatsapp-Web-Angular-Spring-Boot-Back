package whatsapp.web.authentication.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import whatsapp.web.authentication.dto.*;
import whatsapp.web.authentication.enumeracoes.RolesUsers;
import whatsapp.web.authentication.enumeracoes.StatusResponse;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whatsapp.web.config.service.CookieService;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.configuration.cookie-set-secure}")
    private Boolean cookieSetSecure;

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    private final CookieService cookieService;

    public ResponseEntity<ResponseLoginDTO> login(LoginDTO loginDTO, HttpServletResponse response){
        String email = loginDTO.email();
        String password = loginDTO.password();
        Boolean rememberPassword = loginDTO.rememberPassword();
        Integer expirationToken = 1;

        if(email == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseLoginDTO(
                            StatusResponse.ERROR,
                            "Nenhum e-mail foi enviado.",
                            "email", null
                    ));
        }

        if(password == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseLoginDTO(
                            StatusResponse.ERROR,
                            "Nenhuma senha foi enviada.",
                            "password", null
                    ));
        }

        Usuario usuario = userService.encontrarPorEmail(email);
        if(usuario == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseLoginDTO(
                            StatusResponse.ERROR,
                            "O e-mail enviado não está atrelado a nenhum usuário.",
                            "email", null
                    ));
        }

        String senhaCriptografada = usuario.getPassword();
        boolean senhasBatem = encoder.matches(password, senhaCriptografada);

        if(!senhasBatem){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ResponseLoginDTO(
                            StatusResponse.ERROR,
                            "Acesso negado. A senha está incorreta.",
                            "password", null
                    ));
        }

        if(rememberPassword){
            expirationToken = 5;
        }

        try {
            String token = tokenService.generateToken(usuario, expirationToken);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(this.cookieSetSecure)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(Duration.ofDays(expirationToken))
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseLoginDTO(
                            StatusResponse.SUCCESS,
                            "Login feito com sucesso.", null, token
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseLoginDTO(
                            StatusResponse.ERROR,
                            "Erro ao gerar o token.",
                            "system", null
                    ));
        }
    }

    public ResponseEntity<ResponseRegisterDTO> registro(RegistroDTO registroDTO){
        String username = registroDTO.username();
        String email = registroDTO.email();
        String name = registroDTO.name();
        String password1 = registroDTO.password1();
        String password2 = registroDTO.password2();
        List<RolesUsers> roles = registroDTO.roles();

        if(username == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "username",
                            "Nenhum username foi enviado."));
        }

        if(name == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "name",
                            "Nenhum nome foi enviado."));
        }

        if(email == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "email",
                            "Nenhum email foi enviado."));
        }

        if(password1 == null || password2 == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "passwords",
                            "As duas senhas não foram preenchidas."));
        }

        if(roles == null || roles.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "roles",
                            "Nenhuma permissão foi enviada."));
        }

        if(username.length() < 5){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "username",
                            "O username deve ter no mínimo 5 caracteres."));
        }

        Usuario usuarioEmail = userService.encontrarPorEmail(email);
        if(usuarioEmail != null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "email",
                            "Esse e-mail já está sendo usado."));
        }

        Usuario usuarioUsername = userService.encontrarPorUsername(username);
        if(usuarioUsername != null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "username",
                            "Esse nome de usuário já está sendo usado."));
        }

        if(!password1.equals(password2)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "passwords",
                            "As senhas não são iguais."));
        }

        if(password1.length() < 8){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseRegisterDTO(
                            StatusResponse.ERROR,
                            "password",
                            "A senha precisa ter no mínimo 8 caracteres."));
        }

        Usuario novoUsuario = userService.salvarUsuario(registroDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseRegisterDTO(
                        StatusResponse.SUCCESS,
                        null,
                        "Usuário criado com sucesso."));
    }

    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = this.cookieService.getCookie("token", request);
        if(token != null){
            String idUser = tokenService.validateToken(token);
            if(idUser != null) {
                this.cookieService.deleteCookie("token", response);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .build();
            };
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public ResponseEntity<?> isAuthenticated(HttpServletRequest request) {
        String token = this.cookieService.getCookie("token", request);
        if(token != null){
            String idUser = tokenService.validateToken(token);
            if(idUser != null) return ResponseEntity.status(HttpStatus.OK).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
