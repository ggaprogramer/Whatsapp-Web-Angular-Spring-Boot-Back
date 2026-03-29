package whatsapp.web.authentication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import whatsapp.web.authentication.enums.RolesUsers;
import whatsapp.web.authentication.model.Usuario;
import whatsapp.web.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import whatsapp.web.config.service.CookieService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = this.recoverToken("token", request);
        String idUser = tokenService.validateToken(token);

        if(idUser != null){
            Optional<Usuario> usuario = userRepository.findById(UUID.fromString(idUser));

            if(usuario.isPresent()){
                List<RolesUsers> roles = usuario.get().getRoles();
                List<SimpleGrantedAuthority> authorities = roles
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getDescricao()))
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(usuario.get(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else{
                throw new RuntimeException("User not found");
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(String name, HttpServletRequest request){
        /*String authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");*/

        return this.cookieService.getCookie(name, request);
    }

}
