package whatsapp.web.authentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import whatsapp.web.authentication.dto.*;
import whatsapp.web.authentication.security.TokenService;
import whatsapp.web.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return authService.login(loginDTO, response);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterDTO> register(@RequestBody RegistroDTO registroDTODTO){
        return authService.registro(registroDTODTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        return authService.logout(request, response);
    }

    @PostMapping("/is-authenticated")
    public ResponseEntity<?> isAuthenticated(HttpServletRequest request) {
        return authService.isAuthenticated(request);
    }

}
